package beetech.com.wallet.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import beetech.com.wallet.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import beetech.com.wallet.R;

import static beetech.com.wallet.ubersplash.MainActivity_App.signout;
import static java.lang.System.exit;

/**
 * Created by chichikolon on 07.12.2017.
 */

public class logout extends AppCompatActivity {

    private Button logout;
    private LovelyProgressDialog waitingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logout = (Button) findViewById(R.id.logout_button);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(beetech.com.wallet.activity.logout.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out Dialog")
                        .setMessage("Are you sure you want to sign out and close the app ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                signout();
                                finish();
                                try {
                                    Thread.sleep(2000);
                                    exit(0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                // finish();
                return;
            }
        });

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
