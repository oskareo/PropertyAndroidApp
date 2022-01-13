package com.example.madpropertypal.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.model.Report;
import com.example.madpropertypal.model.User;

import java.util.ArrayList;

public class AppDataBase {
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private PropertyDataBase propertyDataBase;

    public AppDataBase(Context context) {
        this.context = context;
    }

    /*DATA BASE NAME AND VERSION , CHANGE VERSION WHILE CHANGE IN TABLE*/
    private static final String DATA_BASE_NAME = "property.db";
    private static final int DATA_BASE_VERSION = 1;

    /*1 Table name */
    private static final String TABLE_USER = "user";
    private static final String TABLE_PROPERTY = "property";
    private static final String TABLE_PROPERTY_REPORT = "report";

    /*2 Columns of table*/

    /*User Table*/
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_CREATION_DATE = "creationDate";

    /*Property Table*/
    private static final String COLUMN_PROPERTY_NUMBER = "number";
    private static final String COLUMN_PROPERTY_NAME = "name";
    private static final String COLUMN_PROPERTY_TYPE = "type";
    private static final String COLUMN_PROPERTY_LEASE_TYPE = "leaseType";
    private static final String COLUMN_PROPERTY_LOCATION = "location";
    private static final String COLUMN_PROPERTY_BEDROOM = "bedrooms";
    private static final String COLUMN_PROPERTY_BATHROOM = "bathrooms";
    private static final String COLUMN_PROPERTY_SIZE = "size";
    private static final String COLUMN_PROPERTY_ASKING_PRICE = "askingPrice";
    private static final String COLUMN_PROPERTY_LOCAL_AMENITIES = "localAmenities";
    private static final String COLUMN_PROPERTY_DESCRIPTION = "description";
    private static final String COLUMN_PROPERTY_FLOORS = "floors";
    private static final String COLUMN_PROPERTY_IMAGE = "image";
    private static final String COLUMN_PROPERTY_USER_ID = "userId";
    private static final String COLUMN_PROPERTY_CREATION_DATE = "creationDate";
    private static final String COLUMN_PROPERTY_IMAGE_LINK = "imageLink";
    private static final String COLUMN_PROPERTY_IS_FAVOURITE = "isFavourite";
    private static final String COLUMN_PROPERTY_IS_UPLOADED = "isUploadedToServer";

    /*Property-Report Table*/
    private static final String COLUMN_REPORT_ID = "reportId";
    private static final String COLUMN_REPORT_PROPERTY_ID = "propertyId";
    private static final String COLUMN_REPORT_DATE_OF_VIEWING = "dateOfViewing";
    private static final String COLUMN_REPORT_INTEREST = "interest";
    private static final String COLUMN_REPORT_OFFER_PRICE = "offerPrice";
    private static final String COLUMN_REPORT_OFFER_EXPIRY_DATE = "offerExpiryDate";
    private static final String COLUMN_REPORT_CONDITIONS_OF_OFFER = "conditionsOfOffer";
    private static final String COLUMN_REPORT_VIEWING_COMMENTS = "viewingComments";

    /*3. To drop a table*/
    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS " + TABLE_USER;
    private static final String DROP_TABLE_PROPERTY = "DROP TABLE IF EXISTS " + TABLE_PROPERTY;
    private static final String DROP_TABLE_REPORT = "DROP TABLE IF EXISTS " + TABLE_PROPERTY_REPORT;

    /*4 To create a table*/

    /*User Table*/
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_USER_ID + " INTEGER," +
                    COLUMN_USER_NAME + " TEXT," +
                    COLUMN_USER_EMAIL + " TEXT," +
                    COLUMN_USER_PASSWORD + " TEXT," +
                    COLUMN_USER_CREATION_DATE + " TEXT)";

    /*Property Table*/
    private static final String CREATE_TABLE_PROPERTY =
            "CREATE TABLE " + TABLE_PROPERTY + " (" +
                    COLUMN_PROPERTY_NUMBER + " INTEGER," +
                    COLUMN_PROPERTY_NAME + " TEXT," +
                    COLUMN_PROPERTY_TYPE + " TEXT," +
                    COLUMN_PROPERTY_LEASE_TYPE + " TEXT," +
                    COLUMN_PROPERTY_LOCATION + " TEXT," +
                    COLUMN_PROPERTY_BEDROOM + " TEXT," +
                    COLUMN_PROPERTY_BATHROOM + " TEXT," +
                    COLUMN_PROPERTY_SIZE + " TEXT," +
                    COLUMN_PROPERTY_ASKING_PRICE + " TEXT," +
                    COLUMN_PROPERTY_LOCAL_AMENITIES + " TEXT," +
                    COLUMN_PROPERTY_DESCRIPTION + " TEXT," +
                    COLUMN_PROPERTY_FLOORS + " TEXT," +
                    COLUMN_PROPERTY_IMAGE + " BLOB," +
                    COLUMN_PROPERTY_USER_ID + " INTEGER," +
                    COLUMN_PROPERTY_CREATION_DATE + " TEXT," +
                    COLUMN_PROPERTY_IMAGE_LINK + " TEXT," +
                    COLUMN_PROPERTY_IS_FAVOURITE + " INTEGER," +
                    COLUMN_PROPERTY_IS_UPLOADED + " INTEGER)";

    /*Property-Report Table*/
    private static final String CREATE_TABLE_PROPERTY_REPORT =
            "CREATE TABLE " + TABLE_PROPERTY_REPORT + " (" +
                    COLUMN_REPORT_ID + " INTEGER," +
                    COLUMN_REPORT_PROPERTY_ID + " INTEGER," +
                    COLUMN_REPORT_DATE_OF_VIEWING + " TEXT," +
                    COLUMN_REPORT_INTEREST + " TEXT," +
                    COLUMN_REPORT_OFFER_PRICE + " TEXT," +
                    COLUMN_REPORT_OFFER_EXPIRY_DATE + " TEXT," +
                    COLUMN_REPORT_CONDITIONS_OF_OFFER + " TEXT," +
                    COLUMN_REPORT_VIEWING_COMMENTS + " TEXT)";


    /*5 Inserting into Table*/
    /*Insert User*/
    public long insertUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_ID, user.getId());
        contentValues.put(COLUMN_USER_NAME, user.getName());
        contentValues.put(COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_USER_CREATION_DATE, String.valueOf(user.getCreationDate()));
        return sqLiteDatabase.insert(TABLE_USER, null, contentValues);
    }

    /*Insert Property*/
    public long insertProperty(Property model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PROPERTY_NUMBER, model.getNumber());
        contentValues.put(COLUMN_PROPERTY_NAME, model.getName());
        contentValues.put(COLUMN_PROPERTY_TYPE, model.getType());
        contentValues.put(COLUMN_PROPERTY_LEASE_TYPE, model.getLeaseType());
        contentValues.put(COLUMN_PROPERTY_LOCATION, model.getLocation());
        contentValues.put(COLUMN_PROPERTY_BEDROOM, model.getBathrooms());
        contentValues.put(COLUMN_PROPERTY_BATHROOM, model.getBedrooms());
        contentValues.put(COLUMN_PROPERTY_SIZE, model.getSize());
        contentValues.put(COLUMN_PROPERTY_ASKING_PRICE, model.getAskingPrice());
        contentValues.put(COLUMN_PROPERTY_LOCAL_AMENITIES, model.getLocalAmenities());
        contentValues.put(COLUMN_PROPERTY_DESCRIPTION, model.getDescription());
        contentValues.put(COLUMN_PROPERTY_FLOORS, model.getFloors());
        contentValues.put(COLUMN_PROPERTY_IMAGE, model.getImage());
        contentValues.put(COLUMN_PROPERTY_USER_ID, model.getUserId());
        contentValues.put(COLUMN_PROPERTY_CREATION_DATE, String.valueOf(System.currentTimeMillis()));
        contentValues.put(COLUMN_PROPERTY_IMAGE_LINK, String.valueOf(model.getImageLink()));
        contentValues.put(COLUMN_PROPERTY_IS_FAVOURITE, model.getIsFavourite());
        contentValues.put(COLUMN_PROPERTY_IS_UPLOADED, model.getIsUploadedToServer());
        return sqLiteDatabase.insert(TABLE_PROPERTY, null, contentValues);
    }

    /*Update Property*/
    public long updateProperty(Property model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PROPERTY_NAME, model.getName());
        contentValues.put(COLUMN_PROPERTY_TYPE, model.getType());
        contentValues.put(COLUMN_PROPERTY_LEASE_TYPE, model.getLeaseType());
        contentValues.put(COLUMN_PROPERTY_LOCATION, model.getLocation());
        contentValues.put(COLUMN_PROPERTY_BEDROOM, model.getBathrooms());
        contentValues.put(COLUMN_PROPERTY_BATHROOM, model.getBedrooms());
        contentValues.put(COLUMN_PROPERTY_SIZE, model.getSize());
        contentValues.put(COLUMN_PROPERTY_ASKING_PRICE, model.getAskingPrice());
        contentValues.put(COLUMN_PROPERTY_LOCAL_AMENITIES, model.getLocalAmenities());
        contentValues.put(COLUMN_PROPERTY_DESCRIPTION, model.getDescription());
        contentValues.put(COLUMN_PROPERTY_FLOORS, model.getFloors());
        contentValues.put(COLUMN_PROPERTY_IMAGE, model.getImage());
        contentValues.put(COLUMN_PROPERTY_USER_ID, model.getUserId());
        contentValues.put(COLUMN_PROPERTY_CREATION_DATE, model.getCreationDate());
        contentValues.put(COLUMN_PROPERTY_IMAGE_LINK, String.valueOf(model.getImageLink()));
        contentValues.put(COLUMN_PROPERTY_IS_FAVOURITE, model.getIsFavourite());
        contentValues.put(COLUMN_PROPERTY_IS_UPLOADED, model.getIsUploadedToServer());
        return sqLiteDatabase.update(TABLE_PROPERTY, contentValues, "number = ?", new String[]{String.valueOf(model.getNumber())});
    }

    /*Insert Report*/
    public long insertReport(Report model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REPORT_ID, model.getReportId());
        contentValues.put(COLUMN_REPORT_PROPERTY_ID, model.getPropertyId());
        contentValues.put(COLUMN_REPORT_DATE_OF_VIEWING, model.getDateOfViewing());
        contentValues.put(COLUMN_REPORT_INTEREST, model.getInterest());
        contentValues.put(COLUMN_REPORT_OFFER_PRICE, String.valueOf(model.getOfferPrice()));
        contentValues.put(COLUMN_REPORT_OFFER_EXPIRY_DATE, String.valueOf(model.getOfferExpiryDate()));
        contentValues.put(COLUMN_REPORT_CONDITIONS_OF_OFFER, String.valueOf(model.getConditionsOfOffer()));
        contentValues.put(COLUMN_REPORT_VIEWING_COMMENTS, String.valueOf(model.getViewingComments()));
        return sqLiteDatabase.insert(TABLE_PROPERTY_REPORT, null, contentValues);
    }

    /*Verify user existence and if user exists then its model will be returned*/
    public User verifyLogin(String email, String password) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user where" + " email = ? AND password = ?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new User(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)))
            );
        } else {
            return null;
        }
    }

    /*Get All Property*/
    public ArrayList<Property> getPropertyList() {
        ArrayList<Property> propertyList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_PROPERTY, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int number = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_NUMBER)));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_NAME));
            String type = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_TYPE));
            String leaseType = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LEASE_TYPE));
            String location = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LOCATION));
            long bedrooms = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_BEDROOM)));
            long bathrooms = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_BATHROOM)));
            long size = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_SIZE)));
            long askingPrice = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_ASKING_PRICE)));
            String localAmenities = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LOCAL_AMENITIES));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_DESCRIPTION));
            long floors = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_FLOORS)));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROPERTY_IMAGE));
            int userId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_USER_ID)));
            long creationDate = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_CREATION_DATE)));
            String imageLink = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IMAGE_LINK));
            int isFavourite = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IS_FAVOURITE)));
            int isUploadedToServer = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IS_UPLOADED)));

            Property property = new Property(number, name, type, leaseType, location, bedrooms,
                    bathrooms, size, askingPrice, localAmenities, description, floors, image, userId,
                    creationDate, imageLink, isFavourite, isUploadedToServer);
            propertyList.add(property);
        }
        return propertyList;
    }

    /*Get All Property of Specific User id*/
    public ArrayList<Property> getPropertyList(int givenUserId) {
        ArrayList<Property> propertyList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from property where" + " userId = ?", new String[]{Integer.toString(givenUserId)});
        while (cursor.moveToNext()) {
            int number = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_NUMBER)));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_NAME));
            String type = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_TYPE));
            String leaseType = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LEASE_TYPE));
            String location = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LOCATION));
            long bedrooms = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_BEDROOM)));
            long bathrooms = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_BATHROOM)));
            long size = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_SIZE)));
            long askingPrice = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_ASKING_PRICE)));
            String localAmenities = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LOCAL_AMENITIES));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_DESCRIPTION));
            long floors = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_FLOORS)));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROPERTY_IMAGE));
            int userId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_USER_ID)));
            long creationDate = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_CREATION_DATE)));
            String imageLink = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IMAGE_LINK));
            int isFavourite = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IS_FAVOURITE)));
            int isUploadedToServer = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IS_UPLOADED)));

            Property property = new Property(number, name, type, leaseType, location, bedrooms,
                    bathrooms, size, askingPrice, localAmenities, description, floors, image,
                    userId, creationDate, imageLink, isFavourite, isUploadedToServer);
            propertyList.add(property);
        }
        return propertyList;
    }

    /*Get All Property of Specific User id and Which are Favourite*/
    public ArrayList<Property> getFavouritePropertyList(int givenUserId) {
        ArrayList<Property> propertyList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from property where" + " userId = ? AND isFavourite = ?", new String[]{Integer.toString(givenUserId), "1"});
        while (cursor.moveToNext()) {
            int number = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_NUMBER)));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_NAME));
            String type = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_TYPE));
            String leaseType = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LEASE_TYPE));
            String location = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LOCATION));
            long bedrooms = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_BEDROOM)));
            long bathrooms = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_BATHROOM)));
            long size = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_SIZE)));
            long askingPrice = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_ASKING_PRICE)));
            String localAmenities = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_LOCAL_AMENITIES));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_DESCRIPTION));
            long floors = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_FLOORS)));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_PROPERTY_IMAGE));
            int userId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_USER_ID)));
            long creationDate = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_CREATION_DATE)));
            String imageLink = cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IMAGE_LINK));
            int isFavourite = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IS_FAVOURITE)));
            int isUploadedToServer = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_IS_UPLOADED)));

            Property property = new Property(number, name, type, leaseType, location, bedrooms,
                    bathrooms, size, askingPrice, localAmenities, description, floors, image,
                    userId, creationDate, imageLink, isFavourite, isUploadedToServer);
            propertyList.add(property);
        }
        return propertyList;
    }

    /*Get All Reports*/
    public ArrayList<Report> getReports(int givenPropertyId) {
        ArrayList<Report> reportsList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from report where" + " propertyId = ?", new String[]{String.valueOf(givenPropertyId)});
        while (cursor.moveToNext()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_ID)));
            int propertyId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_PROPERTY_ID)));
            long dateOfViewing = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_DATE_OF_VIEWING)));
            String interest = cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_INTEREST));
            long offerPrice = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_OFFER_PRICE)));
            long offerExpiryDate = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_OFFER_EXPIRY_DATE)));
            String conditionsOfOffer = cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_CONDITIONS_OF_OFFER));
            String viewingComments = cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_VIEWING_COMMENTS));
            reportsList.add(new Report(id, propertyId, dateOfViewing, interest, offerPrice, offerExpiryDate, conditionsOfOffer, viewingComments));
        }
        return reportsList;
    }

    /*Get All Users*/
    public ArrayList<User> getUsers() {
        ArrayList<User> usersList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_USER, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
            String email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
            long creationDate = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_USER_CREATION_DATE)));
            usersList.add(new User(id, name, email, password, creationDate));
        }
        return usersList;
    }

    /*Check If Table has A user with Same email*/
    public boolean checkUser(String email) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user where" + " email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    public AppDataBase open() throws android.database.SQLException {
        propertyDataBase = new PropertyDataBase(context);
        sqLiteDatabase = propertyDataBase.getWritableDatabase();
        return AppDataBase.this;
    }

    public int getTotalProperties() {
        Cursor cursor = sqLiteDatabase.query(TABLE_PROPERTY, null, null, null, null, null, null);
        return cursor.getCount();
    }

    public int getTotalProperties(int userId) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from property where" + " userId = ?", new String[]{Integer.toString(userId)});
        return cursor.getCount();
    }

    public boolean checkIfPropertyExists(int propertyId) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from property where" + " number = ?", new String[]{Integer.toString(propertyId)});
        return cursor.getCount() > 0;
    }

    public boolean checkIfReportExists(int reportId) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from report where" + " reportId = ?", new String[]{Integer.toString(reportId)});
        return cursor.getCount() > 0;
    }

    public int getTotalFavouriteProperties(int userId) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from property where" + " userId = ? AND isFavourite = ?", new String[]{Integer.toString(userId), "1"});
        return cursor.getCount();
    }

    public int getPendingUploadsProperties() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from property where" + " isUploadedToServer = ?", new String[]{"0"});
        return cursor.getCount();
    }

    public int getTotalReports(int id) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from report where" + " propertyId = ?", new String[]{String.valueOf(id)});
        return cursor.getCount();
    }

    public int getTotalUsers() {
        Cursor cursor = sqLiteDatabase.query(TABLE_USER, null, null, null, null, null, null);
        return cursor.getCount();
    }

    public int deleteProperty(int id) {
        return sqLiteDatabase.delete(TABLE_PROPERTY, "number=?", new String[]{Integer.toString(id)});
    }

    public void close() {
        propertyDataBase.close();
    }

    private class PropertyDataBase extends SQLiteOpenHelper {

        public PropertyDataBase(Context context) {
            super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_USER);
            sqLiteDatabase.execSQL(CREATE_TABLE_PROPERTY);
            sqLiteDatabase.execSQL(CREATE_TABLE_PROPERTY_REPORT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_TABLE_USER);
            sqLiteDatabase.execSQL(DROP_TABLE_PROPERTY);
            sqLiteDatabase.execSQL(DROP_TABLE_REPORT);
        }
    }
}
