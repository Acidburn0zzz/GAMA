package eu.ensg.tsi.azarzelli.gama.io;

import java.io.IOException;

import eu.ensg.tsi.azarzelli.gama.domain.Terrain;

/**
 * File writer interface used to write DEM into raster files
 * @author Amaury
 *
 */
public interface IWriter {
	/**
	 * Writes the Terrain terrain into a file with the full path filename
	 * @param terrain the Terrain to write
	 * @param filename the full file path to write the terrain into
	 * @throws IOException
	 */
	public void write(Terrain terrain, String filename) throws IOException;
}
