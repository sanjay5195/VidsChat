package com.sanju.chat.vidschat.Models;

/**
 * Created by Sanju on 29-Dec-16.
 */

public class User
    {
        public String name="";
        public String email="";
        public String mob_no="";
        public String uid="";
        public String country="";
        public String photoUrl="";

       public User()
        {

        }
       public User(String name,String email,String mob_no,String uid,String country, String photoUrl)
        {
            this.name=name;
            this.email=email;
            this.mob_no=mob_no;
            this.uid=uid;
            this.country=country;
            this.photoUrl = photoUrl;

        }
    }
