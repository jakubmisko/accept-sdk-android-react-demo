package com.wirecard.accept.uicomponents.terminals;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wirecard.accept.uicomponents.R;

import java.util.List;

import static com.wirecard.accept.uicomponents.Preconditions.nullCheck;

/**
 * Created by super on 14.03.2017.
 */

public class TerminalChooser extends DialogFragment {
    private RadioGroup radioGroup;
    private List<String> terminalNames;
    private TerminalsContract terminalsContract;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminals, container, true);
        radioGroup = (RadioGroup) view.findViewById(R.id.terminals);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        nullCheck(terminalNames);
        for (String name : terminalNames){
            RadioButton button = new RadioButton(getActivity());
            button.setText(name);
            radioGroup.addView(button);
        }
    }

    public void handleRadioClick(View view){
        if(((RadioButton) view).isChecked()){
            terminalsContract.onTerminalChoosed(((RadioButton) view).getText().toString());
        }
    }
}
