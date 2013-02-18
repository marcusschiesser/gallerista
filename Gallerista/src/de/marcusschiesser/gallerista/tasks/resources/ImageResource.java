package de.marcusschiesser.gallerista.tasks.resources;

import java.io.IOException;

import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * Interface for accessing a service that provides Images
 * 
 * @author Marcus
 */
public interface ImageResource {

	/**
	 * Uses the resource to perform a query by the given text
	 * 
	 * @param text 
	 * @return
	 * @throws IOException
	 */
	public abstract ImageVO[] getImages(String text) throws IOException;

}