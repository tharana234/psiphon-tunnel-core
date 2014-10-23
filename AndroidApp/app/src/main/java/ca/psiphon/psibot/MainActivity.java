/*
 * Copyright (c) 2014, Psiphon Inc.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ca.psiphon.psibot;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction().add(R.id.container, new LogFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        startVpn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_email_log) {
            Log.composeEmail(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected static final int REQUEST_CODE_PREPARE_VPN = 100;

    protected void startVpn() {
        if (Service.isRunning()) {
            return;
        }
        try {
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_PREPARE_VPN);
            } else {
                startVpnService();
            }
        } catch (ActivityNotFoundException e) {
            Log.addEntry("prepare VPN failed: " + e.getMessage());
        }
    }

    protected void startVpnService() {
        startService(new Intent(this, Service.class));
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (request == REQUEST_CODE_PREPARE_VPN && result == RESULT_OK) {
            startVpnService();
        }
    }
}
