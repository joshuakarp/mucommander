package com.mucommander.ui.dialog.pref.theme;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.mucommander.text.Translator;
import com.mucommander.ui.chooser.FontChooser;
import com.mucommander.ui.chooser.PreviewLabel;
import com.mucommander.ui.dialog.pref.PreferencesDialog;
import com.mucommander.ui.icon.CustomFileIconProvider;
import com.mucommander.ui.icon.IconManager;
import com.mucommander.ui.layout.ProportionalGridPanel;
import com.mucommander.ui.layout.YBoxPanel;
import com.mucommander.ui.quicklist.GenericPopupDataListWithIcons;
import com.mucommander.ui.quicklist.QuickList;
import com.mucommander.ui.quicklist.item.DataList;
import com.mucommander.ui.quicklist.item.HeaderMenuItem;
import com.mucommander.ui.theme.ColorChangedEvent;
import com.mucommander.ui.theme.ThemeData;

/**
 * @author Arik Hadas
 */
public class QuickListPanel extends ThemeEditorPanel implements PropertyChangeListener {
	// - Instance fields -----------------------------------------------------------------
    // -----------------------------------------------------------------------------------
    /** Used to preview the quick list. */
	private JPanel quickListPreviewPanel;
	
	/** The header of the sample quick list */
    private HeaderMenuItem header = new HeaderMenuItem("Sample quick-list");
    
    /** The items of the sample quick list */
    private static Object[] sampleData = new Object[]{"Sample item 1", "Sample item 2", "Sample item 3", "Sample item 4"
		, "Sample item 5", "Sample item 6", "Sample item 7", "Sample item 8", "Sample item 9",
		"Sample item 10", "Sample item 11", "Sample item 12", "Sample item 13", "Sample item 14",};
    
    /** The icon of the sample items */
    private final Icon sampleIcon = IconManager.getIcon(IconManager.FILE_ICON_SET, CustomFileIconProvider.FOLDER_ICON_NAME);
    
    /** The list of items of the sample quick list */
    private DataList list = new GenericPopupDataListWithIcons(sampleData) {
		public Icon getImageIconOfItem(Object item) {
			return sampleIcon;
		}
		
		public void colorChanged(ColorChangedEvent event) {
			super.colorChanged(event);
			repaint();
		}
		
		protected void addMouseListenerToList() {
	    	addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
	        });
	    }
		
		protected void addKeyListenerToList() {
			addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {			
					switch(e.getKeyCode()) {
					case KeyEvent.VK_UP:
						{
							int numOfItems = getModel().getSize();				
							if (numOfItems > 0 && getSelectedIndex() == 0) {
								setSelectedIndex(numOfItems - 1);
								ensureIndexIsVisible(numOfItems - 1);
								e.consume();
							}
						}
						break;
					case KeyEvent.VK_DOWN:
						{
							int numOfItems = getModel().getSize();
							if (numOfItems > 0 && getSelectedIndex() == numOfItems - 1) {				
								setSelectedIndex(0);
								ensureIndexIsVisible(0);
								e.consume();
							}						
						}
						break;
					}
				}

				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
    };
    
    /**
     * Creates the quick list preview panel.
     * @return the quick list preview panel.
     */
    private JPanel createPreviewPanel() {
    	JPanel      panel;  // Preview panel.
        JScrollPane scroll; // Wraps the preview quick list.

        // add JScrollPane that contains the TablePopupDataList to the popup.
		scroll = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);				
		scroll.setBorder(null);		        
		scroll.getVerticalScrollBar().setFocusable( false ); 
        scroll.getHorizontalScrollBar().setFocusable( false );

        // Creates the panel.
        panel = new JPanel();        
        quickListPreviewPanel = new YBoxPanel();        
        quickListPreviewPanel.add(header);
        quickListPreviewPanel.add(scroll);
        quickListPreviewPanel.setBorder(new QuickList.PopupsBorder());
        panel.add(quickListPreviewPanel);
        panel.setBorder(BorderFactory.createTitledBorder(Translator.get("preview")));
        
        return panel;
    }
    
	// - Initialisation ------------------------------------------------------------------
    // -----------------------------------------------------------------------------------
    /**
     * Creates a new quick list editor.
     * @param parent    dialog containing the panel.
     * @param themeData  themeData being edited.
     */
    public QuickListPanel(PreferencesDialog parent, ThemeData themeData) {
        super(parent, Translator.get("quick_lists_menu"), themeData);
        initUI();
    }
	
	// - UI initialisation ---------------------------------------------------------------
    // -----------------------------------------------------------------------------------
    /**
     * Creates the JPanel that contains all of the item's color configuration elements.
     * @param fontChooser font chooser used by the editor panel.
     * @return the JPanel that contains all of the item's color configuration elements.
     */
    private JPanel createItemColorsPanel(FontChooser fontChooser) {
        ProportionalGridPanel gridPanel;   // Contains all the color buttons.
        JPanel                colorsPanel; // Used to wrap the colors panel in a flow layout.
        PreviewLabel          label;

        // Initialisation.
        gridPanel = new ProportionalGridPanel(3);

        // Header.
        addLabelRow(gridPanel, false);

        label = new PreviewLabel();
        
        // Color buttons.
        addColorButtons(gridPanel, fontChooser, "quick_list.normal",
                        ThemeData.QUICK_LIST_ITEM_FOREGROUND_COLOR, ThemeData.QUICK_LIST_ITEM_BACKGROUND_COLOR, label).addPropertyChangeListener(this);
        addColorButtons(gridPanel, fontChooser, "quick_list.selected",
                ThemeData.QUICK_LIST_SELECTED_ITEM_FOREGROUND_COLOR, ThemeData.QUICK_LIST_SELECTED_ITEM_BACKGROUND_COLOR, label).addPropertyChangeListener(this);
        label.addPropertyChangeListener(this);
        
        // Wraps everything in a flow layout.
        colorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorsPanel.add(gridPanel);
        colorsPanel.setBorder(BorderFactory.createTitledBorder(Translator.get("quick_list.colors")));

        return colorsPanel;
    }
    
    /**
     * Creates the JPanel that contains all of the header's color configuration elements.
     * @param fontChooser font chooser used by the editor panel.
     * @return the JPanel that contains all of the header's color configuration elements.
     */
    private JPanel createHeaderColorsPanel(FontChooser fontChooser) {
        ProportionalGridPanel gridPanel;   // Contains all the color buttons.
        JPanel                colorsPanel; // Used to wrap the colors panel in a flow layout.
        PreviewLabel          label;

        // Initialisation.
        gridPanel = new ProportionalGridPanel(3);

        // Header.
        addLabelRow(gridPanel, false);

        label = new PreviewLabel();
        
        // Color buttons.        
        addColorButtons(gridPanel, fontChooser, "",
        		ThemeData.QUICK_LIST_HEADER_FOREGROUND_COLOR, ThemeData.QUICK_LIST_HEADER_BACKGROUND_COLOR, label).addPropertyChangeListener(this);
        label.addPropertyChangeListener(this);
        
        gridPanel.add(createCaptionLabel(""));
        gridPanel.add(new JLabel());
        PreviewLabel label3 = new PreviewLabel();        
        ColorButton butt;
        gridPanel.add(butt = new ColorButton(parent, themeData, ThemeData.QUICK_LIST_HEADER_SECONDARY_BACKGROUND_COLOR, PreviewLabel.BACKGROUND_COLOR_PROPERTY_NAME, label3));//.addPropertyChangeListener(this);
        label3.setTextPainted(true);
        label3.addPropertyChangeListener(this);
        butt.addUpdatedPreviewComponent(label3);

        // Wraps everything in a flow layout.
        colorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorsPanel.add(gridPanel);
        colorsPanel.setBorder(BorderFactory.createTitledBorder(Translator.get("quick_list.colors")));

        return colorsPanel;
    }

	/**
     * Initialises the panel's UI.
     */
    private void initUI() {
        YBoxPanel   headerConfigurationPanel; // Contains all the configuration elements.
        YBoxPanel   itemConfigurationPanel; // Contains all the configuration elements.
        FontChooser fontChooser1;        // Used to select a font.
        FontChooser fontChooser2;        // Used to select a font.
        JPanel      mainPanel;          // Main panel.
        JTabbedPane tabbedPane;
        
        header.addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent e) {}

			public void componentMoved(ComponentEvent e) {}

			public void componentResized(ComponentEvent e) {
				quickListPreviewPanel.repaint();
			}

			public void componentShown(ComponentEvent e) {}
        });


        // Font chooser and preview initialization.
        fontChooser1 = createFontChooser(ThemeData.QUICK_LIST_HEADER_FONT);
        fontChooser2 = createFontChooser(ThemeData.QUICK_LIST_ITEM_FONT);
        addFontChooserListener(fontChooser1, header);
        addFontChooserListener(fontChooser2, list);

        // Header configuration panel initialization.
        headerConfigurationPanel = new YBoxPanel();
        headerConfigurationPanel.add(fontChooser1);
        headerConfigurationPanel.addSpace(10);
        headerConfigurationPanel.add(createHeaderColorsPanel(fontChooser1));

        // Item configuration panel initialization.
        itemConfigurationPanel = new YBoxPanel();
        itemConfigurationPanel.add(fontChooser2);
        itemConfigurationPanel.addSpace(10);
        itemConfigurationPanel.add(createItemColorsPanel(fontChooser1));
        
        // Create the tabbed pane.
        tabbedPane = new JTabbedPane();
        tabbedPane.add(Translator.get("quick_list.header"), headerConfigurationPanel);
        tabbedPane.add(Translator.get("quick_list.item"), itemConfigurationPanel);
        
        // Main layout.
        mainPanel   = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(createPreviewPanel(), BorderLayout.EAST);
        
        // Layout.
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.NORTH);
    }
    
    /**
     * Listens on changes on the foreground and background colors.
     */
    public void propertyChange(PropertyChangeEvent event) {
        // Background color changed.
        if(event.getPropertyName().equals(PreviewLabel.BACKGROUND_COLOR_PROPERTY_NAME)) {
            header.setBackgroundColors(themeData.getColor(ThemeData.QUICK_LIST_HEADER_BACKGROUND_COLOR),
            		themeData.getColor(ThemeData.QUICK_LIST_HEADER_SECONDARY_BACKGROUND_COLOR));
            list.setBackgroundColors(themeData.getColor(ThemeData.QUICK_LIST_ITEM_BACKGROUND_COLOR),
            						 themeData.getColor(ThemeData.QUICK_LIST_SELECTED_ITEM_BACKGROUND_COLOR));
        }

        // Foreground color changed.
        else if(event.getPropertyName().equals(PreviewLabel.FOREGROUND_COLOR_PROPERTY_NAME)) {
            header.setForegroundColor(themeData.getColor(ThemeData.QUICK_LIST_HEADER_FOREGROUND_COLOR));            
            list.setForegroundColors(themeData.getColor(ThemeData.QUICK_LIST_ITEM_FOREGROUND_COLOR),
            						 themeData.getColor(ThemeData.QUICK_LIST_SELECTED_ITEM_FOREGROUND_COLOR));
        }
    }

    // - Modification management ---------------------------------------------------------
    // -----------------------------------------------------------------------------------
    /**
     * Ignored.
     */
    public void commit() {}
}
