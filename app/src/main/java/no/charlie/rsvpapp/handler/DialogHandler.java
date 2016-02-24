package no.charlie.rsvpapp.handler;

/**
 * Created by charlie midtlyng on 19/07/15.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

public class DialogHandler {
    public Runnable submitProcedure = null;

    // Dialog. --------------------------------------------------------------

    public boolean Confirm(Context context, String Title, String ConfirmText,
                           String CancelBtn, String OkBtn, Runnable submitMethod) {
        submitProcedure = submitMethod;
        AlertDialog dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert).create() :
                new AlertDialog.Builder(context).create();
        dialog.setTitle(Title);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        submitProcedure.run();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
        return true;
    }

    public boolean SimpleAlert(Context context, String Title, String ConfirmText, String OkBtn) {
        AlertDialog dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert).create() :
                new AlertDialog.Builder(context).create();
        dialog.setTitle(Title);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
        return true;
    }

}

