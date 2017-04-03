package com.wirecard.accept.uicomponents.terminals;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wirecard.accept.uicomponents.R;

import java.util.ArrayList;
import java.util.List;

import static com.wirecard.accept.uicomponents.Preconditions.nullCheck;

/**
 * Created by super on 14.03.2017.
 */

public class TerminalChooser extends DialogFragment {
    private String TAG = getClass().getSimpleName();
    private RadioGroup radioGroup;
    private List<String> terminalNames;
    private TerminalsContract terminalsContract;

    public static TerminalChooser newInstance(TerminalsContract terminalsContract, ArrayList<String> terminalNames) {

        Bundle args = new Bundle();
        args.putStringArrayList("names", terminalNames);
        TerminalChooser fragment = new TerminalChooser();
        fragment.setTerminalsContract(terminalsContract);
        fragment.setArguments(args);
        return fragment;
    }

    @Deprecated
    public void setTerminalNames(List<String> terminalNames) {
        this.terminalNames = terminalNames;
    }

    public void setTerminalsContract(TerminalsContract terminalsContract) {
        this.terminalsContract = terminalsContract;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminals, container, false);
        radioGroup = (RadioGroup) view.findViewById(R.id.terminals);
        if(getArguments().containsKey("names")){
            terminalNames = getArguments().getStringArrayList("names");
        }
//        if(!softNullCheck(savedInstanceState) && savedInstanceState.containsKey("actualVal")){
//            int retrievedValueId = savedInstanceState.getInt("actualVal");
//            radioGroup.check(retrievedValueId);
//        }
        setCancelable(false);
        Button confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminalsContract.onTerminalChoosed(getChoosenTerminal());
                dismiss();
            }
        });
        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminalsContract.onTerminalNotChoosed();
                dismiss();
            }
        });
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

    private String getChoosenTerminal(){
        int checkedOptionId = radioGroup.getCheckedRadioButtonId();
        nullCheck(getView());
        return ((RadioButton) getView().findViewById(checkedOptionId)).getText().toString();
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("actualValId", radioGroup.getCheckedRadioButtonId());
//    }
}
