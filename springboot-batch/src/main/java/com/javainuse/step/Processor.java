package com.javainuse.step;

import org.springframework.batch.item.ItemProcessor;

import com.javainuse.model.BookDAO;

public class Processor implements ItemProcessor<BookDAO, BookDAO> {

	@Override
	public BookDAO process(BookDAO data) throws Exception {
		
		//data.setContent(data.getContent().replace("\n", "").replace("\r", "")); 
		
		String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
		
		String datafilter = data.getContent();
		datafilter = datafilter.replaceAll(characterFilter,"");
		datafilter = datafilter.replaceAll("[\\uD83D\\uFFFD\\uFE0F\\u203C\\u3010\\u3011\\u300A\\u166D\\u200C\\u202A\\u202C\\u2049\\u20E3\\u300B\\u300C\\u3030\\u065F\\u0099\\u0F3A\\u0F3B\\uF610\\uFFFC]", "");
		datafilter = datafilter.replaceAll("」", "");
		//datafilter = datafilter.replaceAll("[^\\s\\p{L}\\p{N}]+", "");
		data.setContent(datafilter);
		return data;
	}

}
