package com.robocompany.robonitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robocompany.robonitor.Activities.BioSensorMonitorActivity;

import org.jetbrains.annotations.NotNull;

public class RobotListAdapter extends ArrayAdapter<Robot> {

    private Context ctx;
    private int resouce;
    private RobotList robotList;
    private ProgressDialog progressDialog;

    RobotListAdapter(Context ctx, int resource, RobotList robotList){

        super(ctx, resource, robotList);

        this.ctx = ctx;
        this.resouce = resource;
        this.robotList = robotList;

    }

    @NotNull
    @Override
    public View getView(final int position, View convertView, @NotNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View view = inflater.inflate(resouce, null);

        TextView txv_name = view.findViewById(R.id.txv_name);
        TextView txv_addr = view.findViewById(R.id.txv_addr);
        TextView txv_port = view.findViewById(R.id.txv_port);
        ImageButton btn_iRobot = view.findViewById(R.id.btn_iRobot);
        ImageButton btn_del = view.findViewById(R.id.btn_del);
        ProgressBar pb_check = view.findViewById(R.id.pb_check);

        final Robot currobot = robotList.get(position);

        txv_name.setText(currobot.name);
        txv_addr.setText(currobot.address);
        txv_port.setText(Integer.toString(currobot.port));

        if(currobot.state == 0) {       //Unchecked
            btn_iRobot.setImageDrawable(ctx.getDrawable(R.drawable.irobot2_offline));
            pb_check.setVisibility(View.INVISIBLE);
        }
        else if(currobot.state == 1) {  //Checking
            btn_iRobot.setImageDrawable(ctx.getDrawable(R.drawable.irobot2_offline));
            pb_check.setVisibility(View.VISIBLE);
        }
        else if(currobot.state == 2) {  //Checked offline
            btn_iRobot.setImageDrawable(ctx.getDrawable(R.drawable.irobot2_offline));
            pb_check.setVisibility(View.INVISIBLE);
        }
        else if(currobot.state == 3) {  //Checked online
            btn_iRobot.setImageDrawable(ctx.getDrawable(R.drawable.irobot2_online));
            pb_check.setVisibility(View.INVISIBLE);
        }


        btn_iRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currobot.state == 3){

                    progressDialog = new ProgressDialog(ctx);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle(getContext().getString(R.string.ask_password));

                    final EditText input = new EditText(ctx);

                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);


                    builder.setPositiveButton(getContext().getString(R.string.connect), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currobot.auth(input.getText().toString());

                            new Login().execute(currobot);
                        }
                    });
                    builder.setNegativeButton(getContext().getString(R.string.cancell), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRobot(position);
            }
        });

        return view;
    }

    public class Login extends AsyncTask<Robot, Integer, Robot>{

        protected void onPreExecute() {

            progressDialog.setMessage(getContext().getString(R.string.starting));
            progressDialog.setTitle(getContext().getString(R.string.connecting));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        protected void onProgressUpdate (Integer... v){
            int progressvalue = v[0];

            switch (progressvalue){
                case 0:
                    progressDialog.setMessage(getContext().getString(R.string.starting));
                    break;
                case 1:
                    progressDialog.setMessage(getContext().getString(R.string.loggin_in));
                    break;
                case 2:
                    progressDialog.setMessage(getContext().getString(R.string.getting_info));
                    break;
                case 3:
                    progressDialog.setMessage(getContext().getString(R.string.ending));
                    break;
            }
        }

        @Override
        protected Robot doInBackground(Robot... robots) {

            Robot r = robots[0];

            publishProgress(0);

            r.login();
            if(r.loged_in == 1){

                publishProgress(1);

                r.get_info();

                publishProgress(2);

                if(r.loged_in == 3){
                    return r;
                }
                publishProgress(3);
            }
            return r;
        }

        protected void onPostExecute (Robot r){

            if(r.loged_in == 3){

                Intent i = new Intent(ctx, BioSensorMonitorActivity.class);
                //Gson gson = new Gson();
                //String json = gson.toJson(r);
                //i.putExtra("Robot", json);
                i.putExtra("Robot",r);
                ctx.startActivity(i);
                progressDialog.dismiss();
            }
            else{
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(getContext().getString(R.string.error));
                builder.setMessage(getContext().getString(R.string.loggin_error));
                builder.setNegativeButton(getContext().getString(R.string.back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }
    }

    private void removeRobot(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(getContext().getString(R.string.sure_delete));
        builder.setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                robotList.remove(position);
                notifyDataSetChanged();
                robotList.save_list(ctx.getApplicationContext());
            }
        });

        builder.setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
