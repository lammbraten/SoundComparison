package de.hsnr.mdb.WaveFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.hsnr.mdb.FFT.Complex;
import de.hsnr.mdb.FFT.FFT;

public class FeatureWavFile  {

	private WavFile wf;
	double[] hammingWindow;	
	double[] f_k;
		
	private FeatureWavFile(WavFile wf){
		this.wf =  wf;
	}

	public static FeatureWavFile openWavFile(File file) throws IOException, WavFileException{
		FeatureWavFile fwf = new FeatureWavFile(WavFile.openWavFile(file));
		fwf.calcFeatures();
		
		return fwf;
	}
	
	private void calcHammingWindow(int N){
		hammingWindow = new double[N];
		
		for(int i = 0; i < N; i++)
			hammingWindow[i] = 0.54 - 0.46 * Math.cos((2*Math.PI*i)/(N-1));
	}
	
	private void calcf_k(int fs, int N) {
		f_k = new double[N];
		
		for(int k = 0; k < N; k++)
			f_k[k] = k*(fs/N);
	}

	private void calcFeatures() throws IOException, WavFileException {
		int length = (int) wf.getFramesRemaining();//Only use it for small files to  avoid overflows.
		int N = 1024;
		int fs = 44100;
		double[] sampleBuffer = new double[length]; 
		wf.readFrames(sampleBuffer, length);
		
		LinkedList<Frame> frames = new LinkedList<Frame>();
		
		calcHammingWindow(N);
		calcf_k(N, fs);
		
		//Generiere Frames
		for(int i = 0; i < length - N; i += (N/2)){
			frames.add(new Frame(sampleBuffer, i, N));
		}
		
	}


	public WavFile getWavFile() {
		return wf;
	}
	
	class Frame{
		double[] frameSamples;

		double[] energy;
		int start;
		int N;
		
		double loudness;
		double ZC; //Zero-Crossing-Rate
		double bandwith;
		double brightness;
		
		Frame(double[] sampleBuffer, int start, int N){
			this.start = start;
			this.N = N;
			this.frameSamples = new double[N];
			
			for(int i = 0; i < N; i++)
				frameSamples[i] = sampleBuffer[start + i];
			
			calcEnergy();
			
			loudness = calcLoudness();
			ZC = calZeroCrossings();
			bandwith = calcBandwith();
			brightness = calcBrightness();
			
			
		}

		private double calcLoudness() {
			double x = 0.0;
			
			for(int i = 0; i < N; i++)
				x += (frameSamples[i] * frameSamples[i]);
				
			return x/N;
		}
		
		//Couldn't be replaced with std-compare-method
		private int sgn(double x_i){
			if(x_i > 0)
				return 1;
			if(x_i < 0)
				return -1;
			return 0;
					
		}		
		
		private double calZeroCrossings() {
			double x = 0.0;
			
			for(int i = 1; i < N; i++)
				x += Math.abs(sgn(frameSamples[i] - sgn(frameSamples[i-1])));
				
			return x/(2*N);
		}

		private void calcEnergy(){
			Complex []xNew = new Complex[N];
			energy = new double[N];
			
			for(int i = 0; i < N; i++)
				xNew[i] = new Complex((frameSamples[i] * hammingWindow[i]),0);
			
			Complex []X = FFT.fft(xNew);			
			
			for(int i = 0; i < N; i++)
				energy[i] = 10*Math.log10(Math.sqrt((X[i].re()*X[i].re())+
								(X[i].im()*X[i].im())
						));
			
		}
		
		private double calcBrightness(){
			double count = 0.0;
			double energyGes = 0.0;

			for(int k = 0; k < N/2; k++){
				count += f_k[k] * energy[k];
				energyGes +=  energy[k];
			}
			
			
			return count/energyGes;	
		}
		
		private double calcBandwith() {
			double count = 0.0;
			double energyGes = 0.0;
			
			for(int k = 0; k < N/2; k++){
				count += ((f_k[k] - brightness)*(f_k[k] - brightness)) * energy[k];
				energyGes +=  energy[k];
			}
			
			return count/energyGes;	
		}
		
		
	}
}
