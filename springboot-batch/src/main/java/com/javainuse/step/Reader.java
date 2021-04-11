package com.javainuse.step;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.aspose.pdf.internal.imaging.internal.Exceptions.TimeoutException;
import com.javainuse.model.BookDAO;

public class Reader implements ItemReader<BookDAO> {

	private String[] messages = { "https://ln.hako.re" , "https://truyenchu.vn/"};

	
	private String[] path = {"/truyen/5632-magicalexplorer/c76313-chuong-116-cau-chuyen-dang-sau-cuoc-tan-cong-cua-gabby" , "/toan-tri-doc-gia/chuong-1-mo-dau-ba-cach-de-song-sot-trong-mot-the-gioi-do-nat"};
	
	private String[] bookName = {"Magical", "Toan Tri Doc gia"};
	
	private String[] urlClass = {"a.rd_sd-button_item.rd_top-right", "a.btn.btn-success.btn-chapter-nav#next_chap"};
	
	private int count = 0;

	@Override
	public BookDAO read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		if(count >= messages.length) {
			count = 0;
			return null;
		}
		
		StringBuilder book  = new StringBuilder(); 
		Document doc = Jsoup.connect(messages[count]).userAgent("Mozilla").get();  

	    Elements listData =doc.select("p"); 
	    Element link = doc.select(urlClass[count]).first();
	    //String url = messages[count] + path;
	    
	    while(!path[count].isEmpty()) {
	    	try {
	    		doc = Jsoup.connect(messages[count] + path[count]).get();  
	    	}catch(TimeoutException exception) {
	    		doc = Jsoup.connect(messages[count] + path[count]).get(); 
	    	}catch(Exception exception) {
	    		System.out.println("Do nothing with "+ exception);
	    	}
	    	
	    	System.out.println("URL   " + messages[count] + path[count]);
	    	listData =doc.select("p"); 
		    link = doc.select(urlClass[count]).first();
		    System.out.println("urlClass[count] : " + urlClass[count]);
		    System.out.println("link : " + link);
		    if(null == link) {
		    	path[count]  = "";
		    }else {
		    	path[count] = link.attr("href");
		    	System.out.println("URL : " + link.attr("href"));
		    }
		    
	    	
		    for(Element element : listData) {
		    	book.append(element.text());	    	
		    }
		    
		    
		   
	    }
	   
		BookDAO bookDAO  = new BookDAO();
		bookDAO.setContent(book.toString());
		bookDAO.setName(bookName[count]);
		

		count++;
		return bookDAO;
		
	}

}