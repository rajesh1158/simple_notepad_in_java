package com.notepad;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.io.*;

public class Notepad extends JFrame implements ActionListener
{
	//VARIABLES
	private static final long serialVersionUID = 1L;
	private static final String appName = " - SimPad (Simple Notepad)";
	private String fileSavePath = null;
	private boolean isEdited = false;
	private boolean shouldExit = false;
	private boolean shouldCloseSaveBox = false;
	
	//LAYOUT
	//private static JFrame frame = new JFrame();
	private JTextArea textArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private JMenuBar menuBar = new JMenuBar();
	private WordSearcher searcher = new WordSearcher(textArea);

	//FILE MENU
	private JMenu file = new JMenu();
	private JMenuItem newFile = new JMenuItem();
	private JMenuItem openFile = new JMenuItem();
	private JMenuItem saveFile = new JMenuItem();
	private JMenuItem saveAsFile = new JMenuItem();
	private JMenuItem close = new JMenuItem();

	//INFO MENU
	private JMenu info = new JMenu();
	private JMenuItem about = new JMenuItem();

	//EDIT MENU
	private JMenu edit = new JMenu();
	private JMenuItem search = new JMenuItem();

	//CONSTRUCTOR
	Notepad()
	{
		addEntryToFrame();
		
		//TEXT AREA PROPERTIES
		textArea.setFocusable(true);
		textArea.setFont(new Font("Century Gothic", Font.PLAIN, 14));

		//FRAME PROPERTIES
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setSize(800, 500);
		this.setTitle("Untitled" + appName);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(scrollPane);
		this.setJMenuBar(menuBar);

		//ADD MENUS TO MENUBAR
		menuBar.add(file);
		file.setText("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		menuBar.add(edit);
		edit.setText("Edit");
		edit.setMnemonic(KeyEvent.VK_E);
		
		menuBar.add(info);
		info.setText("Info");
		info.setMnemonic(KeyEvent.VK_I);

		//FILE MENU ITEMS
		newFile.setText("New");
		newFile.addActionListener(this);
		newFile.setMnemonic(KeyEvent.VK_N);
		newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		file.add(newFile);
		
		openFile.setText("Open");
		openFile.addActionListener(this);
		openFile.setMnemonic(KeyEvent.VK_O);
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		file.add(openFile);

		saveFile.setText("Save");
		saveFile.addActionListener(this);
		saveFile.setMnemonic(KeyEvent.VK_S);
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		file.add(saveFile);

		saveAsFile.setText("Save As");
		saveAsFile.addActionListener(this);
		saveAsFile.setMnemonic(KeyEvent.VK_A);
		saveAsFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		file.add(saveAsFile);
		saveAsFile.setEnabled(false);

		close.setText("Exit");
		close.setMnemonic(KeyEvent.VK_X);
		close.addActionListener(this);
		file.add(close);

		//EDIT MENU ITEMS
		search.setText("Search");
		search.setMnemonic(KeyEvent.VK_C);
		search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		search.addActionListener(this);
		edit.add(search);
		search.setEnabled(false);

		//INFO MENU ITEMS
		about.setText("About");
		about.setMnemonic(KeyEvent.VK_U);
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		about.addActionListener(this);
		info.add(about);
		
		//ADDING LISTENERS
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveAndClose();
			}
		});
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				modified();
				isEdited = true;
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				modified();
				isEdited = true;
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
			}

			private void modified()
			{
				if(textArea.getText().isEmpty())
				{
					search.setEnabled(false);
				}
				else
				{
					search.setEnabled(true);
				}
			}			
		});

		textArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    searcher.removeAllHighlights();
                }
            }
        });
		
		//MAKE FRAME VISIBLE AFTER ALL ITS COMPONENTS ARE CREATED
		this.setVisible(true);
	}
	
	//NOTEPAD FUNCTIONALITY
	public void saveAndClose()
	{
		if((isEdited && fileSavePath != null) || (!textArea.getText().isEmpty() && fileSavePath == null))
		{
			int whichButtonClicked = JOptionPane.showConfirmDialog(this, "Do you want to save the text before closing ??", "Choose", WindowConstants.DO_NOTHING_ON_CLOSE);
			if(whichButtonClicked == 0)
			{
				saveFile();
			}
			else
			{
				deletePropAndExitAppIfNoFileOpen();
			}
		}
		else
		{
			deletePropAndExitAppIfNoFileOpen();
		}

		if(shouldExit)
		{
			deletePropAndExitAppIfNoFileOpen();
		}
		this.dispose();
		return;
	}

	private void deletePropAndExitAppIfNoFileOpen()
	{
		changeEntryInFrame();
		
		File propFile = new File(TestMain.getPropfiledir(), TestMain.getPropfilename());
		File frameFile = new File(TestMain.getPropfiledir(), TestMain.getFramefilename());
		
		if(propFile.exists() && propFile.length() > 0)
		{
			if(fileSavePath != null)
			{
				delEntryFromProp(fileSavePath);
			}
		}
		
		if(getNumFramesOpen() == 0)
		{			
			propFile.delete();
			frameFile.delete();
			System.exit(0);
		}
		
		return;
	}
	
	private static int getNumFramesOpen()
	{
		File frameFile = new File(TestMain.getPropfiledir(), TestMain.getFramefilename());
		FileReader fileReader;
		int numFrames = 0;

		if(frameFile.exists())
		{
			try
			{
				fileReader = new FileReader(frameFile);
		        BufferedReader bufferReader = new BufferedReader(fileReader);
	        	String str = bufferReader.readLine().trim();
	        	numFrames = Integer.parseInt(str);
		        bufferReader.close();
			}
			catch(Exception e)
			{
			}
		}
				
		return numFrames;
	}
	
	public void saveFile()
	{
		if(fileSavePath == null)
		{
			saveAsFile();
		}
		else
		{
			writeToFile(fileSavePath);
			shouldExit = false;
		}
		return;
	}

	public void saveAsFile()
	{
		JFileChooser save = new JFileChooser();
		int option = save.showSaveDialog(this);
		if(option == JFileChooser.APPROVE_OPTION)
		{
			File file = save.getSelectedFile();
			if(file.exists())
			{
				int whichButtonClicked = JOptionPane.showConfirmDialog(this, "File already exists !! Do you want to over-write existing file ??", "Choose", WindowConstants.DO_NOTHING_ON_CLOSE);
				if(whichButtonClicked == 0)
				{
					callWriteToFile(file);
				}
				else
				{
					saveAsFile();
				}
			}
			else
			{
				callWriteToFile(file);
			}
		}
		else if(option == JFileChooser.CANCEL_OPTION)
		{
			save.setVisible(false);
			shouldExit = false;
		}
		return;
	}

	private void callWriteToFile(File file)
	{
		if(fileSavePath != null)
		{
			delEntryFromProp(fileSavePath);
		}
		
		writeToFile(file.getPath());
		fileSavePath = file.getPath();
		saveAsFile.setEnabled(true);
		
		addEntryToProp(fileSavePath);		
		return;
	}

	private void writeToFile(String path)
	{
		try
		{				
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			out.write(textArea.getText());
			out.flush();
			out.close();

			isEdited = false;
			shouldExit = true;
			this.setTitle(path + appName);
		}
		catch(Exception ex)
		{
		}
		return;
	}

	private static void addEntryToFrame()
	{
		File frameFile = new File(TestMain.getPropfiledir(), TestMain.getFramefilename());
		
		if(frameFile.exists())
		{
			File tempFrameFile = new File(TestMain.getPropfiledir(), "temp_" + TestMain.getFramefilename());
			FileWriter fileWriter;
			FileReader fileReader;
			
			try
			{
				fileReader = new FileReader(frameFile);
				fileWriter = new FileWriter(tempFrameFile);
		        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		        BufferedReader bufferReader = new BufferedReader(fileReader);
		        String data = "";
		        
		        if(frameFile.length() == 0)
		        {
		        	data = "1";
		        }
		        else
		        {
		        	String str = bufferReader.readLine().trim();
		        	int numFrames = Integer.parseInt(str) + 1;
		        	data = Integer.toString(numFrames);
		        }
		        
		        bufferWriter.write(data);
		        bufferWriter.close();
		        bufferReader.close();
		        
		        frameFile.delete();
		        tempFrameFile.renameTo(frameFile);
			}
			catch(IOException e)
			{
			}
		}
		return;
	}

	private static void changeEntryInFrame()
	{
		File frameFile = new File(TestMain.getPropfiledir(), TestMain.getFramefilename());
		
		if(frameFile.exists())
		{
			File tempFrameFile = new File(TestMain.getPropfiledir(), "temp_" + TestMain.getFramefilename());
			FileWriter fileWriter;
			FileReader fileReader;
			
			try
			{
				fileReader = new FileReader(frameFile);
				fileWriter = new FileWriter(tempFrameFile);
		        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		        BufferedReader bufferReader = new BufferedReader(fileReader);
		        String data = "";
		        
		        if(frameFile.length() == 0)
		        {
		        	data = "0";
		        }
		        else
		        {
		        	String str = bufferReader.readLine().trim();
		        	int numFrames = Integer.parseInt(str) - 1;
		        	data = Integer.toString(numFrames);
		        }
		        
		        bufferWriter.write(data);
		        bufferWriter.close();
		        bufferReader.close();
		        
		        frameFile.delete();
		        tempFrameFile.renameTo(frameFile);
			}
			catch(IOException e)
			{
			}
		}
		return;
	}
	
	private static void addEntryToProp(String filePath)
	{
		File propFile = new File(TestMain.getPropfiledir(), TestMain.getPropfilename());
		
		if(propFile.exists())
		{
			FileWriter fileWriter;
			try
			{
				fileWriter = new FileWriter(propFile,true);
		        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		        bufferWriter.write(filePath);
		        bufferWriter.newLine();
		        bufferWriter.close();
			}
			catch(IOException e)
			{
			}
		}
		return;
	}
	
	private static void delEntryFromProp(String filePath)
	{
		File propFile = new File(TestMain.getPropfiledir(), TestMain.getPropfilename());
		
		if(propFile.exists() && filePath != null)
		{
			File tempFile = new File(TestMain.getPropfiledir(), "temp_" + TestMain.getPropfilename());
			FileReader fileReader;
			FileWriter fileWriter;
			try
			{
				fileReader = new FileReader(propFile);
				fileWriter = new FileWriter(tempFile);
				
		        BufferedReader bufferReader = new BufferedReader(fileReader);
		        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		        
		        String openFile = null;
		        
		        while((openFile = bufferReader.readLine()) != null)
		        {
		        	String trimmedLine = openFile.trim();
		        	if(filePath.equalsIgnoreCase(trimmedLine))
		        	{
		        		continue;
		        	}
		        	
		        	bufferWriter.write(openFile);
		        	bufferWriter.newLine();
		        }
		        
		        bufferReader.close();
		        bufferWriter.close();
		        
		        propFile.delete();
		        tempFile.renameTo(propFile);	        
			}
			catch(IOException e)
			{
			}
		}
		return;
	}
	
	public void openFile()
	{
		if(isEdited && !textArea.getText().isEmpty())
		{
			int whichButtonClicked = JOptionPane.showConfirmDialog(this, "Do you want to save the text before opening another file ??", "Choose", WindowConstants.DO_NOTHING_ON_CLOSE);
			if(whichButtonClicked == 0)
			{
				saveFile();
			}
		}

		if(!shouldCloseSaveBox)
		{
			JFileChooser open = new JFileChooser();
			int option = open.showOpenDialog(this);

			if(option == JFileChooser.APPROVE_OPTION)
			{
				File fileToOpen = open.getSelectedFile();
				if(fileToOpen.exists())
				{
					if(!checkIfFileOpen(fileToOpen))
					{
						if(fileSavePath != null)
						{
							delEntryFromProp(fileSavePath);
						}

						textArea.setText("");

						try
						{
							Scanner scan = new Scanner(new FileReader(open.getSelectedFile().getPath()));
							while (scan.hasNext())
							{
								textArea.append(scan.nextLine() + "\n");
							}

							textArea.replaceRange("", textArea.getText().length() - 1, textArea.getText().length());
						}
						catch(Exception ex)
						{
						}

						fileSavePath = open.getSelectedFile().getPath();
						addEntryToProp(fileSavePath);
						saveAsFile.setEnabled(true);
						this.setTitle(fileSavePath + appName);
						isEdited = false;
					}
					else
					{
						JOptionPane.showMessageDialog(this, "File already open in another window !!");
					}
				}
				else
				{
					JOptionPane.showMessageDialog(this, "File does not exist !!");
				}
			}
		}
		shouldCloseSaveBox = false;
		return;
	}

	private boolean checkIfFileOpen(File file)
	{
		File propFile = new File(TestMain.getPropfiledir(), TestMain.getPropfilename());
		FileReader fileReader;
		boolean flag = false;

		if(propFile.exists())
		{
			try
			{
				fileReader = new FileReader(propFile);
		        BufferedReader bufferReader = new BufferedReader(fileReader);
		        
		        String fileToCheck = file.getPath();
		        String fileFromProp = "";
		        
		        while((fileFromProp = bufferReader.readLine()) != null)
		        {
		        	String trimmedLine = fileFromProp.trim();
		        	if(fileToCheck.equalsIgnoreCase(trimmedLine))
		        	{
		        		flag = true;
		        		break;
		        	}
		        }
		        
		        bufferReader.close();
			}
			catch(IOException e)
			{
			}
		}
		return flag;
	}
	
	public void showInfo()
	{
		String msg = "Product: Simpad (Simple Notepad)\n";
		msg += "Developer: Rajesh Kumar\n";
		msg += "E-mail: p-rajeshkumar@hcl.com";

		JOptionPane.showMessageDialog(this, msg);
		return;
	}

	public void searchText()
	{		
		String searchString = JOptionPane.showInputDialog(this, "Enter text to search");
		
		while(searchString!= null && searchString.isEmpty())
		{
			searchString = JOptionPane.showInputDialog(this, "Enter text to search");
		}

		if(searchString != null)
		{
			int offset = searcher.searchAndHighlight(searchString);
			if(offset != -1)
			{
				try
				{
					textArea.scrollRectToVisible(textArea.modelToView(offset));
				}
				catch(BadLocationException e)
				{
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "No match found for below text:\n"+ searchString);
			}
		}
		return;
	}
	
	public void newFile()
	{
		if(isEdited && !textArea.getText().isEmpty())
		{
			int whichButtonClicked = JOptionPane.showConfirmDialog(this, "Do you want to save the text before opening new empty file ??", "Choose", WindowConstants.DO_NOTHING_ON_CLOSE);
			if(whichButtonClicked == 0)
			{
				saveFile();
			}
		}
		
		if(!shouldCloseSaveBox && fileSavePath != null)
		{
			delEntryFromProp(fileSavePath);
			
			fileSavePath = null;
			saveAsFile.setEnabled(false);
			this.setTitle("Untitled" + appName);
			isEdited = false;
			textArea.setText("");
		}
		shouldCloseSaveBox = false;
		return;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newFile)
		{
			newFile();
		}
		else if(e.getSource() == close)
		{
			saveAndClose();
		}
		else if(e.getSource() == openFile)
		{
			openFile();
		}
		else if(e.getSource() == saveFile)
		{
			saveFile();
		}
		else if(e.getSource() == saveAsFile)
		{
			saveAsFile();
		}
		else if(e.getSource() == about)
		{
			showInfo();
		}
		else if(e.getSource() == search)
		{
			searchText();
		}
		
		return;
	}	
}