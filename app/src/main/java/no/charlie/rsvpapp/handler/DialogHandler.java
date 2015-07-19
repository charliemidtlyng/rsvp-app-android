package no.charlie.rsvpapp.handler;

/**
 * Created by charlie midtlyng on 19/07/15.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHandler {
    public Runnable submitProcedure = null;

    // Dialog. --------------------------------------------------------------

    public boolean Confirm(Context context, String Title, String ConfirmText,
                           String CancelBtn, String OkBtn, Runnable submitMethod) {
        submitProcedure = submitMethod;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
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
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
    }
}

