package zw.co.learniverse.payload.request;

import lombok.Data;
import zw.co.learniverse.enums.Status;


import java.util.Date;
import java.util.UUID;

@Data
public class StudentUpdateRequest {


    // User fields
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;

    //teacher other details
    private String phonenumber;
    private String houseNumber;
    private String street;
    private String city;
    private String country;
    private String qualification;
    private Date qualificationDate;
    private String expertise;
    private Status status;
    private Date dob;
    private String bloodGroup;
    private UUID levelId;
    private UUID termId;
  ;

}
