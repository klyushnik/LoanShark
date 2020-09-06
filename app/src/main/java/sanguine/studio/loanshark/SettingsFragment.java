package sanguine.studio.loanshark;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;


public class SettingsFragment extends Fragment{
    final String DB_EXPORT_PATH = "";
    final int REQUEST_CODE_DB = 999;
    int fileOperationMode = 0;
    //EditText pinEditText;
    //Switch pinSwitch, showWithInterestSwitch;
    Switch showWithInterestSwitch;
    //Button importButton, exportButton, applyButton;
    //Button applyButton;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Util.PermissionAskListener permissionListener;
    Context mContext;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        //pinEditText = view.findViewById(R.id.settings_pinEditText);
        //pinSwitch = view.findViewById(R.id.settings_enablePinSwitch);
        //importButton = view.findViewById(R.id.settings_importDbButton);
        //exportButton = view.findViewById(R.id.settings_exportDbButton);
        //applyButton = view.findViewById(R.id.settings_applyButton);
        showWithInterestSwitch = view.findViewById(R.id.settings_showWithInterestSwitch);
        preferences = getContext().getSharedPreferences("appSettings", Context.MODE_PRIVATE);

        mContext = getContext();

        /*pinSwitch.setChecked(preferences.getBoolean("hasPassword", false));
        if(pinSwitch.isChecked()){
            try{
                String password = AESUtils.decrypt(preferences.getString("password", ""));
                pinEditText.setText(password);
            }catch(Exception e){
                pinSwitch.setChecked(false);
            }
        }*/

        showWithInterestSwitch.setChecked(preferences.getBoolean("showWithInterest", false));
        showWithInterestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor = preferences.edit();
                editor.putBoolean("showWithInterest", compoundButton.isChecked());
                editor.apply();
            }
        });

        /*applyButton.setOnClickListener((view1 -> {
            editor = preferences.edit();
            if(pinSwitch.isChecked()){
                String password = pinEditText.getText().toString();
                if(password.isEmpty()){
                    editor.putBoolean("hasPassword", false);
                    editor.putString("password", "");
                    editor.apply();
                    Toast.makeText(getContext(), "Password is empty!", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        password = AESUtils.encrypt(password);
                        editor.putBoolean("hasPassword", true);
                        editor.putString("password", password);
                        editor.apply();
                        Toast.makeText(getContext(), "Password settings saved", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        editor.putBoolean("hasPassword", false);
                        editor.putString("password", "");
                        editor.apply();
                        Toast.makeText(getContext(), "Error setting the password", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                editor.putBoolean("hasPassword", false);
                editor.putString("password", "");
                editor.apply();
                Toast.makeText(getContext(), "Password settings saved", Toast.LENGTH_SHORT).show();
            }
        }));*/

        //permissionsListeners();
        //TODO: backup/restore databases

        return view;
    }

   /* public void permissionsListeners() {
        permissionListener = new Util.PermissionAskListener() {
            @Override
            public void onPermissionAsk() {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_DB
                );
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                //show a dialog explaining permission and then request permission
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Permission Required");
                dialog.setMessage("This app needs WRITE EXTERNAL STORAGE permission to import or export the database. Grant permission now?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Grant Permission", ((dialogInterface, i) -> {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_DB
                    );
                }));
                dialog.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));
                dialog.show();
            }
            @Override
            public void onPermissionDisabled() {
                AlertDialog.Builder permissionDialog = new AlertDialog.Builder(mContext);
                permissionDialog.setTitle("Permission Denied");
                permissionDialog.setMessage("You need to grant WRITE EXTERNAL STORAGE permission in the app settings to import or export the database. Open the app settings now?");
                permissionDialog.setCancelable(true);
                permissionDialog.setPositiveButton("Open Settings", ((dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }));
                permissionDialog.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()));
                permissionDialog.show();
            }
            @Override
            public void onPermissionGranted() {
                if(fileOperationMode == 0)
                    exportDatabase();
                else
                    importDatabase();
            }
        };

        exportButton.setOnClickListener(view ->{
            fileOperationMode = 0;
            Util.checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionListener);
        });

        importButton.setOnClickListener(view ->{
            fileOperationMode = 1;
            Util.checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionListener);
        });
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        super.onRequestPermissionsResult(RC, per, PResult);

        switch (RC) {
            case REQUEST_CODE_DB:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext,"Permission Granted", Toast.LENGTH_LONG).show();
                    if(fileOperationMode == 0)
                        exportDatabase();
                    else
                        importDatabase();
                } else {
                    Toast.makeText(getContext(),"Permission Canceled, you need to grant it to export database.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void exportDatabase(){


    }

    private void importDatabase(){

    }*/

}
