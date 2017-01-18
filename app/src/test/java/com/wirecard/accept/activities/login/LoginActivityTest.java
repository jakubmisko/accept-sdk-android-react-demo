package com.wirecard.accept.activities.login;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;

import com.wirecard.accept.BaseTest;
import com.wirecard.accept.R;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowAlertDialog;

import static junit.framework.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by super on 06.07.2016.
 */
public class LoginActivityTest extends BaseTest {
    @Test
    public void wrongLogin() {
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().visible().get();
        EditText userName = (EditText) activity.findViewById(R.id.username);
        EditText password = (EditText) activity.findViewById(R.id.password);
        Button login = (Button) activity.findViewById(R.id.login);

        login.performClick();
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        assertEquals("Login error", shadowAlertDialog.getTitle());
        userName.setText("test");
        login.performClick();
        shadowAlertDialog = shadowOf(dialog);
        assertEquals("Login error", shadowAlertDialog.getTitle());
        password.setText("test");
        login.performClick();
        shadowAlertDialog = shadowOf(dialog);
        assertEquals("Login error", shadowAlertDialog.getTitle());
    }

}
