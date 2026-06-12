package com.nextpeyk.mobileapp.core.network.interceptors;

import com.nextpeyk.mobileapp.core.datastore.AppPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<AppPreferences> appPreferencesProvider;

  public AuthInterceptor_Factory(Provider<AppPreferences> appPreferencesProvider) {
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(appPreferencesProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<AppPreferences> appPreferencesProvider) {
    return new AuthInterceptor_Factory(appPreferencesProvider);
  }

  public static AuthInterceptor newInstance(AppPreferences appPreferences) {
    return new AuthInterceptor(appPreferences);
  }
}
