package de.marcusschiesser.gallerista.tasks.resources;

import de.marcusschiesser.gallerista.vo.ImageVO;

public interface ImageResource {

	public abstract ImageVO[] getImages(String text);

}