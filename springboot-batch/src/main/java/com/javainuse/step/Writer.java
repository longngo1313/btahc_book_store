package com.javainuse.step;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.batch.item.ItemWriter;

import com.aspose.pdf.Document;
import com.aspose.pdf.HtmlLoadOptions;
import com.javainuse.model.BookDAO;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resources;

public class Writer implements ItemWriter<BookDAO> {
	
	public static int gapSizeFont = 240;

	@Override
	public void write(List<? extends BookDAO> books) throws Exception {
		System.out.println("15081991  --------write --------------  " + books.size());
		for (BookDAO msg : books) {
			System.out.println("Writing the data " + msg.getContent());
			PDDocument doc = null;
			try {
				doc = new PDDocument();
				PDPage page = new PDPage();
				doc.addPage(page);
				PDPageContentStream contentStream = new PDPageContentStream(doc, page);

				PDType0Font pdfFont = PDType0Font.load(doc, new FileInputStream("C:/Windows/Fonts/times.ttf"), false); // check
																														// that
																														// the
				float fontSize = 11;
				float leading = 1.5f * fontSize;

				PDRectangle mediabox = page.getMediaBox();
				float margin = 72;
				float width = mediabox.getWidth() - 2 * margin;
				float startX = mediabox.getLowerLeftX() + margin;
				float startY = mediabox.getUpperRightY() - margin;

				String text = msg.getContent();
				List<String> lines = new ArrayList<String>();
				int lastSpace = -1;

				/*
				 * float fontHeight =
				 * pdfFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 *
				 * fontSize;
				 */
				float fontHeight = ( pdfFont.getFontDescriptor().getCapHeight()) / 1000 * fontSize; 

				float totalHeight = margin + gapSizeFont;
				float a4Height = mediabox.getHeight();
				// float a4Height= PDRectangle.A4.getHeight();

				System.out.println("a4Height   15081991 ------------         " + a4Height);
				System.out.println("totalHeight   15081991 ------------         " + totalHeight);
				StringBuilder nonSymbolBuffer = new StringBuilder();
				for (char character : text.toCharArray()) {
					if (isCharacterEncodeable(pdfFont, character)) {
						nonSymbolBuffer.append(character);
					} else {
						// handle writing line with symbols...
					}
				}

				text = nonSymbolBuffer.toString();

				while (text.length() > 0) {
					int spaceIndex = text.indexOf(' ', lastSpace + 1);
					if (spaceIndex < 0)
						spaceIndex = text.length();
					String subString = text.substring(0, spaceIndex);
					
					if (totalHeight > a4Height) {
						contentStream.beginText();
						contentStream.setFont(pdfFont, fontSize);
						contentStream.newLineAtOffset(startX, startY);
						
						for (String line: lines) { 
							//if(line.compareTo("line_break") == 0) {
								
								contentStream.showText(line); 
								contentStream.newLineAtOffset(0, -leading); 
							//} 
						}
						
						
						contentStream.endText();
						contentStream.close();

						lines = new ArrayList<String>();

						page = new PDPage();
						doc.addPage(page);
						totalHeight = margin + gapSizeFont;
						contentStream = new PDPageContentStream(doc, page);
					}
					
					if (subString.contains("line_break")) {
						
						if (lastSpace < 0)
							lastSpace = spaceIndex;
						subString = text.substring(0, lastSpace);
						//subString.replace("line_break", "");
						
						String finalState = subString;
						finalState.replace("line_break", "");
						if(!finalState.isEmpty() && !finalState.contains("line_break")) {
							lines.add(finalState);
						}						
						text = text.substring(lastSpace).trim();
						lastSpace = -1;
						totalHeight += fontHeight;
						continue;
					}

					float size = fontSize * pdfFont.getStringWidth(subString) / 1000;

					if (size > width) {
						if (lastSpace < 0)
							lastSpace = spaceIndex;
						subString = text.substring(0, lastSpace);
						lines.add(subString);
						text = text.substring(lastSpace).trim();
						lastSpace = -1;
						totalHeight += fontHeight;
					} else if (spaceIndex == text.length()) {
						lines.add(text);
						text = "";
						totalHeight += fontHeight;
					} else {
						lastSpace = spaceIndex;
					}

				}

				contentStream.beginText();
				contentStream.setFont(pdfFont, fontSize);
				contentStream.newLineAtOffset(startX, startY);
				for (String line : lines) {
					//System.out.println("line   15081991 ------------         " + line);
					contentStream.showText(line);
					contentStream.newLineAtOffset(0, -leading);
				}
				contentStream.endText();
				contentStream.close();

				doc.save(msg.getName() + ".pdf");
			} finally {
				if (doc != null) {
					doc.close();
				}
			}
		}
	}

	private boolean isCharacterEncodeable(PDType0Font currentFont, char character) throws IOException {
		try {
			currentFont.encode(Character.toString(character));
			return true;
		} catch (IllegalArgumentException iae) {
			System.out.println("Can not render");
			return false;
		}
	}

}