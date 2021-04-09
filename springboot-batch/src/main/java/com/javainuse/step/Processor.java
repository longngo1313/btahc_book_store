package com.javainuse.step;

import org.springframework.batch.item.ItemProcessor;

import com.javainuse.model.BookDAO;

public class Processor implements ItemProcessor<BookDAO, BookDAO> {

	@Override
	public BookDAO process(BookDAO data) throws Exception {
		
		//data.setContent(data.getContent().replace("\n", "").replace("\r", "")); 
		
		String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
		data.setContent(data.getContent().replaceAll(characterFilter,""));
		return data;
	}

}
