package org.eclipse.om2m.commons.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.om2m.commons.constants.ShortName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = ShortName.URI_LIST)
public class URIList {

	@XmlList
	@XmlValue
	protected List<String> listOfUri;

	/**
	 * @return the listOfUri
	 */
	public List<String> getListOfUri() {
		if(listOfUri == null){
			listOfUri = new ArrayList<String>();
		}
		return listOfUri;
	}

	/**
	 * @param listOfUri the listOfUri to set
	 */
	public void setListOfUri(List<String> listOfUri) {
		this.listOfUri = listOfUri;
	}
	
}
