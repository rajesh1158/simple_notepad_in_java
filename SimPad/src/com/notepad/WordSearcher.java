package com.notepad;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

// A simple class that searches for a word in
// a document and highlights occurrences of that word

public class WordSearcher
{
	JTextArea tx = null;
	Highlighter highlighter = null;

	public WordSearcher(JTextArea tx)
	{
		this.tx = tx;
		highlighter = tx.getHighlighter();
	}

	// Search for a word and return the offset of the
	// first occurrence. Highlights are added for all
	// occurrences found.
	public int searchAndHighlight(String word)
	{
		int firstOffset = -1;
		
		removeAllHighlights();

		if (word == null || word.equals(""))
		{
			return -1;
		}

		// Look for the word we are given - insensitive search
		String content = null;
		try
		{
			Document d = tx.getDocument();
			content = d.getText(0, d.getLength()).toLowerCase();
		}
		catch(BadLocationException e)
		{
			// Cannot happen
			return -1;
		}

		word = word.toLowerCase();
		int lastIndex = 0;
		int wordSize = word.length();

		while((lastIndex = content.indexOf(word, lastIndex)) != -1)
		{
			int endIndex = lastIndex + wordSize;
			try
			{
				highlighter.addHighlight(lastIndex, endIndex, DefaultHighlighter.DefaultPainter);
			}
			catch(BadLocationException e)
			{
				// Nothing to do
			}
			if(firstOffset == -1)
			{
				firstOffset = lastIndex;
			}
			
			lastIndex = endIndex;
		}

		return firstOffset;
	}

	public void removeAllHighlights()
	{
		// Remove any existing highlights for last word
		Highlighter.Highlight[] highlights = highlighter.getHighlights();
		
		for (int i = 0; i < highlights.length; i++)
		{
			Highlighter.Highlight h = highlights[i];
			highlighter.removeHighlight(h);
		}
	}
}