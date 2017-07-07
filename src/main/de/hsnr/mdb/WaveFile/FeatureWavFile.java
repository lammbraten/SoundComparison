package de.hsnr.mdb.WaveFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import de.hsnr.mdb.FFT.Complex;
import de.hsnr.mdb.FFT.FFT;

public class FeatureWavFile {

	private WavFile wf;
	private String name;
	float[] hammingWindow;
	float[] f_k;
	private LinkedList<Frame> frames;

	private FeatureWavFile(WavFile wf) {
		this.wf = wf;
	}

	public static FeatureWavFile openWavFile(File file) throws IOException, WavFileException {
		FeatureWavFile fwf = new FeatureWavFile(WavFile.openWavFile(file));
		fwf.name = file.getName();
		fwf.calcFeatures();

		return fwf;
	}

	private void calcHammingWindow(int N) {
		hammingWindow = new float[N];

		for (int i = 0; i < N; i++)
			hammingWindow[i] = (float) (0.54f - 0.46f * Math.cos((2 * Math.PI * i) / (N - 1)));
	}

	private void calcf_k(int fs, int N) {
		f_k = new float[N];

		for (int k = 0; k < N; k++)
			f_k[k] = (float) k * ((float) fs / N);
	}

	private void calcFeatures() throws IOException, WavFileException {
		int length = (int) wf.getFramesRemaining();// Only use it for small
													// files to avoid overflows.
		int N = 1024;
		int fs = 44100;
		double[] sampleBuffer = new double[length];
		wf.readFrames(sampleBuffer, length);

		frames = new LinkedList<Frame>();

		calcHammingWindow(N);
		calcf_k(N, fs);

		for (int i = 0; i < length - N; i += (N / 2)) {
			frames.add(new Frame(sampleBuffer, i, N));
		}

	}

	public float[] getLoudnes() {
		float[] loudness = new float[frames.size()];

		for (int i = 0; i < frames.size(); i++)
			loudness[i] = frames.get(i).loudness;

		return loudness;
	}

	public float[] getZCR() {
		float[] zcr = new float[frames.size()];

		for (int i = 0; i < frames.size(); i++)
			zcr[i] = frames.get(i).ZC;

		return zcr;
	}

	public float[] getBrightness() {
		float[] brightness = new float[frames.size()];

		for (int i = 0; i < frames.size(); i++)
			brightness[i] = frames.get(i).brightness;

		return brightness;
	}

	public float[] getBandwidth() {
		float[] bandwidth = new float[frames.size()];

		for (int i = 0; i < frames.size(); i++)
			bandwidth[i] = frames.get(i).bandwidth;

		return bandwidth;
	}

	public String getName() {
		return this.name;
	}

	public WavFile getWavFile() {
		return wf;
	}

	class Frame {
		float[] frameSamples;

		float[] energy;
		int start;
		int N;

		float loudness;
		float ZC; // Zero-Crossing-Rate
		float bandwidth;
		float brightness;

		Frame(double[] sampleBuffer, int start, int N) {
			this.start = start;
			this.N = N;
			this.frameSamples = new float[N];

			for (int i = 0; i < N; i++)
				frameSamples[i] = (float) sampleBuffer[start + i];

			calcEnergy();

			loudness = calcLoudness();
			ZC = calcZeroCrossings();
			bandwidth = calcBandwith();
			brightness = calcBrightness();

		}

		private float calcLoudness() {
			float x = 0.0f;

			for (int i = 0; i < N; i++)
				x += (frameSamples[i] * frameSamples[i]);

			return x / N;
		}

		// Couldn't be replaced with std-compare-method
		private int sgn(float x_i) {
			if (x_i > 0)
				return 1;
			if (x_i < 0)
				return -1;
			return 0;

		}

		private float calcZeroCrossings() {
			float x = 0.0f;

			for (int i = 1; i < N; i++)
				x += Math.abs(sgn(frameSamples[i] - sgn(frameSamples[i - 1])));

			return x / (2 * N);
		}

		private void calcEnergy() {
			Complex[] xNew = new Complex[N];
			energy = new float[N];

			for (int i = 0; i < N; i++)
				xNew[i] = new Complex((frameSamples[i] * hammingWindow[i]), 0);

			Complex[] X = FFT.fft(xNew);

			for (int i = 0; i < N; i++)
				energy[i] = (float) (10 * Math.log10(Math.sqrt((X[i].re() * X[i].re()) + (X[i].im() * X[i].im()))));

		}

		private float calcBrightness() {
			float count = 0.0f;
			float energyGes = 0.0f;

			for (int k = 0; k < N / 2; k++) {
				count += f_k[k] * energy[k];
				energyGes += energy[k];
			}

			return count / energyGes;
		}

		private float calcBandwith() {
			float count = 0.0f;
			float energyGes = 0.0f;

			for (int k = 0; k < N / 2; k++) {
				count += ((f_k[k] - brightness) * (f_k[k] - brightness)) * energy[k];
				energyGes += energy[k];
			}

			return count / energyGes;
		}

	}
}
