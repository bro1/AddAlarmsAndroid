package nz.theysay.addalarms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import nz.theysay.addalarms.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSetalarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = binding.textviewFirst;
                String str = et.getText().toString();
                setAlarms(str);

            }
        });

        binding.buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = binding.textviewFirst;
                et.setText("");
                binding.textviewWarning.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void setAlarms(String str) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String[] lines = str.split("\n");

        int c = 0;

        boolean warningDone = false;

        for (String l : lines) {
            String lv = l.trim();

            if (!lv.isEmpty()) {
                int pos = lv.indexOf(" ");

                String timestr;
                String title;

                if (pos == -1) {
                    timestr = lv;
                    title = "";
                } else {
                    timestr = lv.substring(0, pos);
                    title = lv.substring(pos + 1);
                }

                System.out.println("'" +  timestr +  "'");
                System.out.println("'" + title +  "'");

                Date dt = null;
                try {
                    dt = sdf.parse(timestr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dt == null) continue;

                final Date d = dt;

                if (!warningDone) {
                    LocalDateTime aaa = d.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    int h = aaa.getHour();
                    if (h == 23 || h <= 5) {
                        binding.textviewWarning.setVisibility(View.VISIBLE);
                        warningDone = true;
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setAlarm(d, title);
                    }
                }, c*1000);

                c++;

            }
        }


    }

    private void setAlarm(Date dt, String title) {

        Calendar cal =  GregorianCalendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(dt);
        cal.add(Calendar.MINUTE, -2);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minute );
        i.putExtra(AlarmClock.EXTRA_MESSAGE, title);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        i.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        i.putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT);
        startActivity(i);
    }

}