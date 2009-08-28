package com.idega.presentation.file;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.file.FileStatisticsProvider;
import com.idega.core.contact.data.Email;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableBodyRowGroup;
import com.idega.presentation.TableHeaderRowGroup;
import com.idega.presentation.TableRow;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.GenericButton;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public abstract class FileDownloadStatisticsViewer extends Block {

	public static final String PARAMETER_FILE_ID = "fileId";
	public static final String PARAMETER_FILE_HASH = "fileHash";
	
	private AdvancedProperty file;
	private ICFile realFile;
	
	private String fileHolderIdentifier;
	
	@Override
	public void main(IWContext iwc) throws Exception {
		ELUtil.getInstance().autowire(this);
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
	
		Layer container = new Layer();
		add(container);
		container.setStyleClass("fileDownloadStatisticsViewerStyle");
		
		if (!hasRights(iwc)) {
			container.add(new Heading1(iwrb.getLocalizedString("download_stats.no_rights", "Sorry, you do not have rights to view statistics")));
			return;
		}
		
		AdvancedProperty file = getFile(iwc);
		ICFile realFile = getRealFile(iwc);
		
		Collection<User> downloaders = realFile == null ? null : realFile.getDownloadedBy();
		
		if (file != null) {
			container.add(new Heading3(new StringBuilder(iwrb.getLocalizedString("download_stats.downloads_statistics_for", "Download statistics for"))
				.append(": ").append(URLDecoder.decode(file.getValue(), CoreConstants.ENCODING_UTF8)).toString()));
			
			container.add(new Break());
		}
		
		if (!ListUtil.isEmpty(downloaders)) {
			Table2 table = new Table2();
			container.add(table);
			TableHeaderRowGroup headerRows = table.createHeaderRowGroup();
			TableRow row = headerRows.createRow();
			row.createCell().add(new Text(iwrb.getLocalizedString("download_stats.nr", "Nr")));
			row.createCell().add(new Text(iwrb.getLocalizedString("download_stats.name", "Name")));
			row.createCell().add(new Text(iwrb.getLocalizedString("download_stats.personal_id", "Personal ID")));
			row.createCell().add(new Text(iwrb.getLocalizedString("download_stats.email", "E-mail")));
			
			int index = 0;
			TableBodyRowGroup bodyRows = table.createBodyRowGroup();
			for (User downloader: downloaders) {
				row = bodyRows.createRow();
				
				row.createCell().add(new Text(new StringBuilder().append(index + 1).append(CoreConstants.DOT).toString()));
				row.createCell().add(new Text(downloader.getName()));
				row.createCell().add(new Text(downloader.getPersonalID()));
				String emailAddress = getEmailAddress(iwc, downloader);
				row.createCell().add(StringUtil.isEmpty(emailAddress) ?
					new Text(CoreConstants.MINUS) :
					new Link(emailAddress, new StringBuilder("mailto:").append(emailAddress).toString()));
				
				index++;
			}
		}
		
		addNotifier(iwc, container, file, downloaders);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<User> getPotentialDownloaders(IWContext iwc) {
		Map<String, ? extends FileStatisticsProvider> beans = WebApplicationContextUtils.getWebApplicationContext(iwc.getServletContext())
															.getBeansOfType(FileStatisticsProvider.class);
		if (beans == null || beans.isEmpty()) {
			return null;
		}
		
		Collection<User> allDownloaders = new ArrayList<User>();
		for (FileStatisticsProvider statisticsProvider: beans.values()) {
			Collection<User> downloaders = statisticsProvider.getPotentialDownloaders(getFileHolderIdentifier());
			if (!ListUtil.isEmpty(downloaders)) {
				for (User downloader: downloaders) {
					if (!allDownloaders.contains(downloader)) {
						allDownloaders.add(downloader);
					}
				}
			}
		}
		
		return allDownloaders;
	}
	
	public void addNotifier(IWContext iwc, Layer container, AdvancedProperty file, Collection<User> downloaders) {
		Collection<User> usersToInform = getUsersToInform(iwc, downloaders);
		if (ListUtil.isEmpty(usersToInform) && ListUtil.isEmpty(downloaders)) {
			Layer noReaders = new Layer();
			container.add(noReaders);
			noReaders.add(new Heading3(getMessageNobodyIsInterested(iwc)));
			return;
		}
		
		if (!ListUtil.isEmpty(usersToInform)) {
			container.add(getNotifierButton(iwc, file, usersToInform));
		}
	}
	
	public abstract String getMessageNobodyIsInterested(IWContext iwc);
	
	protected UIComponent getNotifierButton(IWContext iwc, AdvancedProperty file, Collection<User> usersToInform) {
		Layer notificationContainer = new Layer();
		notificationContainer.setStyleClass("fileDownloadStatisticsNotificationContainer");
		
		GenericButton notify = new GenericButton("sendNotifications",
				getResourceBundle(iwc).getLocalizedString("download_stats.send_remind", "Remind readers to download this document"));
		
		String action = getNotifierAction(iwc, file, usersToInform);
		if (StringUtil.isEmpty(action)) {
			return notificationContainer;
		}
		
		notificationContainer.add(notify);
		notify.setOnClick(action);
		
		return notificationContainer;
	}
	
	public abstract String getNotifierAction(IWContext iwc, AdvancedProperty file, Collection<User> usersToInform);
	
	public Collection<User> getUsersToInform(IWContext iwc, Collection<User> downloaders) {
		Collection<User> potentialDownloaders = getPotentialDownloaders(iwc);
		if (ListUtil.isEmpty(potentialDownloaders)) {
			return null;
		}
		
		Collection<User> usersToInform = null;
		if (ListUtil.isEmpty(downloaders)) {
			usersToInform = potentialDownloaders;
		} else {
			usersToInform = new ArrayList<User>();
			for (User reader: potentialDownloaders) {
				if (!downloaders.contains(reader)) {
					usersToInform.add(reader);
				}
			}
		}
		
		return usersToInform;
	}
	
	@SuppressWarnings("unchecked")
	private String getEmailAddress(IWContext iwc, User user) {
		Collection<Email> mails = user.getEmails();
		if (ListUtil.isEmpty(mails)) {
			return null;
		}
		return mails.iterator().next().getEmailAddress();
	}
	
	protected ICFile getRealFile(IWContext iwc) {
		ICFile file = getRealFile();
		if (file != null) {
			return file;
		}
		
		ICFileHome fileHome = null;
		try {
			fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (fileHome == null) {
			return null;
		}
		
		if (iwc.isParameterSet(PARAMETER_FILE_ID)) {
			try {
				file = fileHome.findByPrimaryKey(iwc.getParameter(PARAMETER_FILE_ID));
			} catch(FinderException e) {
				Logger.getLogger(FileDownloadStatisticsViewer.class.getName()).warning("File was not found by id: " + iwc.getParameter(PARAMETER_FILE_ID));
			}
		}
		
		if (file == null && iwc.isParameterSet(PARAMETER_FILE_HASH)) {
			try {
				file = fileHome.findByHash(Integer.valueOf(iwc.getParameter(PARAMETER_FILE_HASH)));
			} catch(FinderException e) {
				Logger.getLogger(FileDownloadStatisticsViewer.class.getName()).warning("File was not found by hash: " + iwc.getParameter(PARAMETER_FILE_HASH));
			}
		}
		
		if (file != null) {
			setRealFile(file);
		}
		
		return file;
	}
	
	protected AdvancedProperty getFile(IWContext iwc) {
		ICFile icFile = getRealFile(iwc);
		if (icFile == null) {
			return null;
		}
		
		AdvancedProperty file = new AdvancedProperty(icFile.getId(), icFile.getName());
		setFile(file);
		return file;
	}
	
	public AdvancedProperty getFile() {
		return file;
	}

	public void setFile(AdvancedProperty file) {
		this.file = file;
	}

	public abstract boolean hasRights(IWContext iwc);
	
	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

	public String getFileHolderIdentifier() {
		return fileHolderIdentifier;
	}

	public void setFileHolderIdentifier(String fileHolderIdentifier) {
		this.fileHolderIdentifier = fileHolderIdentifier;
	}

	public ICFile getRealFile() {
		return realFile;
	}

	public void setRealFile(ICFile realFile) {
		this.realFile = realFile;
	}
	
}
