package com.wirecard.accept;

import android.widget.Button;

import com.wirecard.accept.activities.login.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by super on 06.07.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 24)
public class LoginTest {
    @Test
    public void wrongLogin(){
        LoginActivity activity = Robolectric.setupActivity(LoginActivity.class);

//        EditText userName = (EditText) activity.findViewById(R.id.username);
//        EditText password = (EditText) activity.findViewById(R.id.password);
        Button login = (Button) activity.findViewById(R.id.login);
        login.performClick();

        assertTrue(shadowOf(activity).getNextStartedActivity().equals(activity));
//        Shadows.shadowOf(activity).

    }
}
