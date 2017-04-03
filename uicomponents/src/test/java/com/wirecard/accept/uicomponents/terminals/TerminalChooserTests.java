package com.wirecard.accept.uicomponents.terminals;

import android.widget.RadioGroup;

import com.wirecard.accept.uicomponents.BuildConfig;
import com.wirecard.accept.uicomponents.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

/**
 * Created by super on 19.03.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TerminalChooserTests {

    @Test
    public void verifyTerminalNames(){
        ArrayList<String> names = new ArrayList<>();
        names.add("One");
        names.add("Two");
        names.add("Three");
        TerminalChooser terminalChooser = TerminalChooser.newInstance(null, names);
        FragmentTestUtil.startFragment(terminalChooser);
        assertNotNull(terminalChooser);
        RadioGroup radioGroup = (RadioGroup) terminalChooser.getView().findViewById(R.id.terminals);
    }
}
