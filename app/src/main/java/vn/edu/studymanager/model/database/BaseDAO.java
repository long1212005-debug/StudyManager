package vn.edu.studymanager.model.database;

import android.content.Context;

public class BaseDAO {
    private DBHelper dbHelper;

    public BaseDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }
}
