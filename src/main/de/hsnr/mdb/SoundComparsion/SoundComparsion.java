package de.hsnr.mdb.SoundComparsion;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;

import de.hsnr.mdb.WaveFile.FeatureWavFile;
import de.hsnr.mdb.WaveFile.WavFileException;

public class SoundComparsion {

	public static void main(String[] args) throws IOException, WavFileException {
		Set<FeatureWavFile> wavFiles = read(new File(args[0]));
		
		
		for(FeatureWavFile fwf : wavFiles){
			fwf.getWavFile().display();
			ScatterPlotWindow spw = new ScatterPlotWindow("Test", fwf.getLoudnes());
			spw.pack();
			RefineryUtilities.centerFrameOnScreen(spw);
			spw.setVisible(true);
		}

		for(FeatureWavFile fwf : wavFiles)
			fwf.getWavFile().close();

	}



	private static Set<FeatureWavFile> read(File file) throws IOException, WavFileException {
		if(!file.isDirectory())
			throw new IOException("Must be folder!");
		
		Set<FeatureWavFile> wavFiles = new HashSet<FeatureWavFile>();
		for(File f : file.listFiles())
			wavFiles.add(FeatureWavFile.openWavFile(f));
		
		return wavFiles;
	}

}
