package com.limegroup.gnutella.gui.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JPopupMenu;

import com.frostwire.GuiFrostWireUtils;
import com.frostwire.bittorrent.settings.BittorrentSettings;
import com.frostwire.bittorrent.websearch.clearbits.ClearBitsItem;
import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.util.PopupUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;

public class ClearBitsSearchResult extends AbstractSearchResult {
	public static String redirectUrl=null;
	
	private ClearBitsItem _item;
	private SearchInformation _info;
	
	public ClearBitsSearchResult(ClearBitsItem item, SearchInformation info) {
		_item = item;
		_info = info;
	}
	
	@Override
	public long getCreationTime() {
		//2010-07-15T16:02:42Z
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		long result = System.currentTimeMillis();
		try {
			result = date.parse(_item.created_at).getTime();
		} catch (ParseException e) {
		}
		return result;
	}

	@Override
	public String getExtension() {
		return "torrent";
	}

	@Override
	public String getFileName() {
		String titleNoTags = _item.title.replace("<b>", "").replace("</b>", "");
		return  titleNoTags + ".torrent";
	}

	@Override
	public String getFilenameNoExtension() {
		return "<html>"+_item.title+"</html>";
	}

	@Override
	public String getHost() {
		return "http://www.clearbits.net/";
	}

	@Override
	public int getQuality() {
		return QualityRenderer.EXCELLENT_QUALITY;
	}

	public String getHash() {
		return _item.hashstr;
	}

	@Override
	public int getSecureStatus() {
		return 0;
	}

	@Override
	public long getSize() {
		return Long.valueOf(_item.mb_size * 1024 * 1024);
	}

	@Override
	public float getSpamRating() {
		return 0;
	}

	@Override
	public int getSpeed() {
		return Integer.MAX_VALUE-2;
	}

	@Override
	public String getVendor() {
		return "ClearBits";
	}

	@Override
	public LimeXMLDocument getXMLDocument() {
		return null;
	}

	@Override
	public void initialize(TableLine line) {
		line.setAddedOn(getCreationTime());
		int seeds = Integer.valueOf(_item.seeds);
		line.initLocations(seeds);
	}

	@Override
	public boolean isMeasuredSpeed() {
		return false;
	}

	@Override
	public void takeAction(TableLine line, GUID guid, File saveDir,
			String fileName, boolean saveAs, SearchInformation searchInfo) {
		GUIMediator.instance().openTorrentURI(_item.torrent_url);
		
		showTorrentDetails(BittorrentSettings.SHOW_TORRENT_DETAILS_DELAY);
	}
	
	public void showTorrentDetails(long delay) {
		GuiFrostWireUtils.showTorrentDetails(delay,redirectUrl,_info.getQuery(),_item.location,getFileName());
	}

	@Override
	public JPopupMenu createMenu(JPopupMenu popupMenu, TableLine[] lines,
			boolean markAsSpam, boolean markAsNot, ResultPanel resultPanel) {
		
		PopupUtils.addMenuItem(I18n.tr("Buy this item now"), resultPanel.BUY_LISTENER, 
    			popupMenu, lines.length == 1, 0);
        PopupUtils.addMenuItem(SearchMediator.DOWNLOAD_STRING, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				takeAction(null, null, null, null, false, null);
			}},
                popupMenu, lines.length > 0, 1);
        
        PopupUtils.addMenuItem(I18n.tr("Torrent Details"), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showTorrentDetails(0);
			}}, popupMenu, lines.length == 1, 2);
        
        return popupMenu;
	}

}
