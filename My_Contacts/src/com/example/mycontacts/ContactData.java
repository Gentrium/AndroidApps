package com.example.mycontacts;

import java.io.Serializable;

/**
 * Class for data.
 */
@SuppressWarnings("serial")
public class ContactData implements Serializable {
	
	
	/**
	 * Instantiates a new contact data.
	 *
	 * @param id the id
	 * @param name the name
	 * @param lastname the lastname
	 * @param date the date
	 * @param adress the adress
	 * @param gender the gender
	 * @param image the image
	 */
	public ContactData(long id, String name, String lastname, long date, 
			String adress, String gender, String image){
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.date = date;
		this.adress = adress;
		this.gender = gender;
		this.image = image;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {return id;}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public long getDate(){return date;}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){return name;}
	
	/**
	 * Gets the lastname.
	 *
	 * @return the lastname
	 */
	public String getLastname(){return lastname;}
	
	/**
	 * Gets the adress.
	 *
	 * @return the adress
	 */
	public String getAdress() {return adress;}
	
	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public String getGender() {return gender;}
	
	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public String getImage() {return image;}
	
	private long id;
	private long date;
	private String name;
	private String lastname;
	private String adress;
	private String gender;
	private String image;
	

}
