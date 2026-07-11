package com.project.ongil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/** Opens the installed TMAP app with a destination selected by ONGIL. */
public final class TmapNavigationLauncher {

    private static final String TMAP_PACKAGE = "com.skt.tmap.ku";
    private static final String PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=" + TMAP_PACKAGE;

    private TmapNavigationLauncher() {
    }

    /**
     * Requests driving guidance in the external TMAP application.
     * The coordinate is deliberately supplied by the pickup-zone recommendation,
     * rather than using a hardcoded route from the UI.
     */
    public static boolean openRoute(Context context, String destinationName,
                                    double latitude, double longitude) {
        Uri routeUri = new Uri.Builder()
                .scheme("tmap")
                .authority("route")
                .appendQueryParameter("goalname", destinationName)
                .appendQueryParameter("goalx", String.valueOf(longitude))
                .appendQueryParameter("goaly", String.valueOf(latitude))
                .build();

        Intent routeIntent = new Intent(Intent.ACTION_VIEW, routeUri);
        routeIntent.setPackage(TMAP_PACKAGE);

        if (routeIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(routeIntent);
            return true;
        }

        Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL));
        if (storeIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(storeIntent);
        }
        return false;
    }
}
