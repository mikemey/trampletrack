package at.mm.trampletrack.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class NumberInputDialog {

	public NumberInputDialog(Context ctx, int startNumber, String title, String message, final INumberDialogListener listener) {
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		alert.setTitle(title);
		alert.setMessage(message);

		final EditText input = new EditText(ctx);
		input.setText(startNumber + "");
		input.setKeyListener(new DigitsKeyListener());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int newNumber = Integer.parseInt(input.getText().toString());
				listener.ready(newNumber);
			}
		});
		alert.setNegativeButton("Cancel", null);
		alert.show();
	}
}
