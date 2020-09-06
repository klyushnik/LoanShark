package sanguine.studio.loanshark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class DashboardActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Button borrowersButton, creditorsButton, historyButton, settingsButton, importExportButton, contactsButton;

        LinearLayout rootLayout = findViewById(R.id.dashboardRootLinearLayout); //this needs to be first so that lockscreen can find it
        rootLayout.setPadding(0,Util.GetStatusBarHeight(this),0,0); //set padding so that logo is moved down a bit

        borrowersButton = findViewById(R.id.borrowersButton);
        borrowersButton.setOnClickListener(this);
        creditorsButton = findViewById(R.id.creditorsButton);
        creditorsButton.setOnClickListener(this);
        historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(this);
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);
        importExportButton = findViewById(R.id.manualButton);
        importExportButton.setOnClickListener(this);
        contactsButton = findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {    //button onCLick
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", v.getId());
        startActivityForResult(intent, 1);
    }

    //lockscreen listeners

}
