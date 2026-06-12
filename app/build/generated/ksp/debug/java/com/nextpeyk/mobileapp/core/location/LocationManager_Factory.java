package com.nextpeyk.mobileapp.core.location;

import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class LocationManager_Factory implements Factory<LocationManager> {
  private final Provider<Context> contextProvider;

  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public LocationManager_Factory(Provider<Context> contextProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.contextProvider = contextProvider;
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  @Override
  public LocationManager get() {
    return newInstance(contextProvider.get(), fusedLocationClientProvider.get());
  }

  public static LocationManager_Factory create(Provider<Context> contextProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new LocationManager_Factory(contextProvider, fusedLocationClientProvider);
  }

  public static LocationManager newInstance(Context context,
      FusedLocationProviderClient fusedLocationClient) {
    return new LocationManager(context, fusedLocationClient);
  }
}
