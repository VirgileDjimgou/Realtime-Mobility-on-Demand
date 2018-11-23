package com.android.gudana.tindroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.tindroid.db.BaseDb;
import com.android.gudana.tindroid.media.VxCard;
import com.android.gudana.tindroid.widgets.RoundImageDrawable;

import co.tinode.tinodesdk.MeTopic;
import co.tinode.tinodesdk.NotConnectedException;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for editing current user details.
 */
public class AccountInfos_Activity extends AppCompatActivity {

    private static final String TAG = "AccountInfoFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tin_fragment_account_info);

        //AccountInfos_Activity.this.setHasOptionsMenu(true);
        Log.d(TAG, "AccountInfoFragment.onCreateView");

        // Inflate the fragment layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.account_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // toolbar.setTitle(R.string.account_settings);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        MeTopic<VxCard> me = Cache.getTinode().getMeTopic();
        if (me != null) {
            final AppCompatActivity activity = AccountInfos_Activity.this;
            final AppCompatImageView avatar = activity.findViewById(R.id.imageAvatar);
            final TextView title = activity.findViewById(R.id.topicTitle);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAccountTitle();
                }
            });

            findViewById(R.id.uploadAvatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // requestAvatar_from_Activity(Activity activity_)
                    UiUtils.requestAvatar_from_Activity(AccountInfos_Activity.this);
                }
            });

            VxCard pub = me.getPub();
            if (pub != null) {
                if (!TextUtils.isEmpty(pub.fn)) {
                    title.setText(pub.fn);
                    title.setTypeface(null, Typeface.NORMAL);
                    title.setTextIsSelectable(true);
                } else {
                    title.setText(R.string.placeholder_contact_title);
                    title.setTypeface(null, Typeface.ITALIC);
                    title.setTextIsSelectable(false);
                }
                final Bitmap bmp = pub.getBitmap();
                if (bmp != null) {
                    avatar.setImageDrawable(new RoundImageDrawable(bmp));
                }
            }
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);

            final Switch readrcpt = activity.findViewById(R.id.switchReadReceipts);
            readrcpt.setChecked(pref.getBoolean(UiUtils.PREF_READ_RCPT, true));
            readrcpt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    pref.edit().putBoolean(UiUtils.PREF_READ_RCPT, isChecked).apply();
                }
            });

            final Switch typing = activity.findViewById(R.id.switchTypingNotifications);
            typing.setChecked(pref.getBoolean(UiUtils.PREF_TYPING_NOTIF, true));
            typing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    pref.edit().putBoolean(UiUtils.PREF_TYPING_NOTIF, isChecked).apply();
                }
            });

            ((TextView) activity.findViewById(R.id.topicAddress)).setText(Cache.getTinode().getMyId());

            activity.findViewById(R.id.buttonChangePassword).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder
                            .setTitle(R.string.change_password)
                            .setView(R.layout.tin_dialog_password)
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TextView editor = (TextView)
                                            ((AlertDialog) dialog).findViewById(R.id.enterPassword);
                                    if (editor != null) {
                                        String password = editor.getText().toString();
                                        if (!TextUtils.isEmpty(password)) {
                                            changePassword(pref.getString(LoginActivity.PREFS_LAST_LOGIN, null),
                                                    password);
                                        } else {
                                            Toast.makeText(activity, R.string.failed_empty_password,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                }
            });
            activity.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });

            final TextView auth = activity.findViewById(R.id.authPermissions);
            auth.setText(me.getAuthAcsStr());
            auth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            final TextView anon = activity.findViewById(R.id.anonPermissions);
            anon.setText(me.getAnonAcsStr());
            anon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    // Dialog for editing pub.fn and priv
    private void showEditAccountTitle() {
        final MeTopic<VxCard> me = (MeTopic<VxCard>) Cache.getTinode().getMeTopic();
        VxCard pub = me.getPub();
        final String title = pub == null ? null : pub.fn;
        final Activity activity = this;
        if (activity == null) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View editor = LayoutInflater.from(builder.getContext()).inflate(R.layout.tin_dialog_edit_account, null);
        builder.setView(editor).setTitle(R.string.edit_account);

        final EditText titleEditor = editor.findViewById(R.id.editTitle);
        titleEditor.setText(title);

        builder
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UiUtils.updateTitle(AccountInfos_Activity.this, me, titleEditor.getText().toString(), null);
                    }
                })

                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void changePassword(String login, String password) {
        Log.d(TAG, "Change password: " + login + ", " + password);

        Activity activity = this;
        try {
            // TODO: update stored record on success
            Cache.getTinode().updateAccountBasic(null, login, password).thenApply(
                    null, new UiUtils.ToastFailureListener(activity)
            );
        } catch (NotConnectedException ignored) {
            Toast.makeText(activity, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {
            Toast.makeText(activity, R.string.action_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        final Activity activity = AccountInfos_Activity.this;
        if (activity == null) {
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton(android.R.string.cancel, null)
                .setMessage(R.string.confirm_logout)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BaseDb.getInstance().logout();
                        Cache.invalidate();
                        startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        final MeTopic me = Cache.getTinode().getMeTopic();
        if (requestCode == UiUtils.SELECT_PICTURE && resultCode == RESULT_OK) {
            UiUtils.updateAvatar(this, me, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(AccountInfos_Activity.this, MainActivity_GuDDana.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }
    */
}
