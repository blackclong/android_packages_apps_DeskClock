package com.shendu.ringtone;

import com.android.deskclock.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
/**
 * setting phone ringtone options
 * @author liuyongsheng
 *
 */
public class SoundPreference extends RingtonePreference  implements
        PreferenceManager.OnActivityResultListener {
    
    private Context mContext;
    
    public SoundPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setMSummary();
    }
    
    private void setMSummary(){
        setMSummary(null);
    }
    /**
     * setting preference summary tips display
     * 
     */
    private void setMSummary(Uri mUri){
        Uri uri = null;
        if(mUri == null){
            String uriString = Settings.System.getString(
                    mContext.getContentResolver(),
                    Settings.System.RINGTONE);
            if(uriString!=null){
                uri = Uri.parse(uriString);
            }else{
            	return;
            }
        }else{
            uri = mUri;
        }
        Ringtone mRingtone = RingtoneManager.getRingtone(mContext,uri);
        if(mRingtone!=null){
            mRingtone.getTitle(mContext);
            String summary  = null;
            if (uri.getPathSegments().size() > 1) {
                summary = uri.getPathSegments().get(0);
                if("internal".equals(summary)){
                    summary = mContext.getString(R.string.sound_ringtones);
                    loadMediaPath = 0 ;
                }else if("external".equals(summary)){
                    summary = mContext.getString(R.string.sound_music);
                    loadMediaPath = 1 ;
                }
            }
            setSummary(summary + ": " + mRingtone.getTitle(mContext));
        }
    }

    public SoundPreference(Context context) {
        super(context);
        mContext = context;
    }
    
    @Override
    protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        setShowDefault(false);
        setShowSilent(false);
        super.onPrepareRingtonePickerIntent(ringtonePickerIntent);
        /*
         * Since this preference is for choosing the default ringtone, it
         * doesn't make sense to show a 'Default' item.
         */
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        
        String uriString = Settings.System.getString(
                mContext.getContentResolver(),
                Settings.System.RINGTONE);
            ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
               uriString != null ? Uri.parse(uriString) : null);
        /**
         * loadMediaPath : load ringtone list from sdcard or /system/media
         * loadMediaTile : dialog display title
         */
        ringtonePickerIntent.putExtra("loadMediaPath", loadMediaPath);
        ringtonePickerIntent.putExtra("loadMediaTile", loadMediaTile);
    }

    @Override
    protected void onSaveRingtone(Uri ringtoneUri) {
        /**onclick submit saved ringtone next step display soundPreference summary*/
        if(ringtoneUri==null){
            return;
        }
        setAlert(ringtoneUri);
        if (mChangeDefault) {
        	 // Update the default alert in the system.
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.ALARM_ALERT,
                    ringtoneUri == null ? null : ringtoneUri.toString());
        }
        
        /*Ringtone mRingtone = RingtoneManager.getRingtone(mContext,ringtoneUri);
        if(mRingtone!=null){
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), getRingtoneType(), ringtoneUri);
            setMSummary(ringtoneUri);
        }else{
            setMSummary(null);
            if( !ringtoneUri.toString().equals(Settings.System.getString(
                    mContext.getContentResolver(),
                    Settings.System.RINGTONE)) ){
                Toast.makeText(getContext(),R.string.file_not_find, 1).show();
            }
        }*/
    }

    @Override
    protected Uri onRestoreRingtone() {
    	   if (RingtoneManager.isDefault(mAlert)) {
               return RingtoneManager.getActualDefaultRingtoneUri(getContext(),
                       RingtoneManager.TYPE_ALARM);
           }
       return mAlert;
    }
    
    private int loadMediaPath = 0;
    private String loadMediaTile;
    
    /**
     * create dialog options 
     * onclick next call super onClick();
     */
    @Override
    protected void onClick() {
        // TODO Auto-generated method stub
        Builder builder = new AlertDialog.Builder(mContext);
  
        //builder.setIcon(R.drawable.ic_dialog_sound);
        builder.setTitle(R.string.alert);
        builder.setSingleChoiceItems(R.array.ringtone_entries, loadMediaPath, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        loadMediaPath = 0;
                        loadMediaTile = mContext.getString(R.string.sound_ringtones);
                        closeDialog(dialog);
                        SoundPreference.super.onClick();
                        break;
                    case 1:
                        loadMediaPath = 1;
                        loadMediaTile = mContext.getString(R.string.sound_music);
                        closeDialog(dialog);
                        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                            SoundPreference.super.onClick();
                        }else{
                            Toast.makeText(getContext(), R.string.sdcard_not_find, 1).show();
                        }
                        break;
                    default:
                        loadMediaPath = 0;
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.alarm_alert_dismiss_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeDialog(dialog);
            }
        });
        builder.show();
    }
    
    private void closeDialog(DialogInterface dialog){
        if(dialog!=null){
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
    }
    
    /////////////////////////////////////////////////
    private boolean mChangeDefault;
    
    private AsyncTask mRingtoneTask;
    
    private Uri mAlert;
    
    public void setAlert(Uri alert) {
        mAlert = alert;
        if (alert != null) {
            setSummary(R.string.loading_ringtone);
            if (mRingtoneTask != null) {
                mRingtoneTask.cancel(true);
            }
            mRingtoneTask = new AsyncTask<Uri, Void, String>() {
                @Override
                protected String doInBackground(Uri... params) {
                    Ringtone r = RingtoneManager.getRingtone(
                            getContext(), params[0]);
                    if (r == null) {
                        r = RingtoneManager.getRingtone(getContext(),
                                Settings.System.DEFAULT_ALARM_ALERT_URI);
                    }
                    if (r != null) {
                        return r.getTitle(getContext());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String title) {
                    if (!isCancelled()) {
                        setSummary(title);
                        mRingtoneTask = null;
                    }
                }
            }.execute(alert);
        } else {
            setSummary(R.string.silent_alarm_summary);
        }
    }
    
    
    public Uri getAlert() {
        return mAlert;
    }

    public void setChangeDefault() {
        mChangeDefault = true;
    }
    ///////////////////////////////////////////////
    
}