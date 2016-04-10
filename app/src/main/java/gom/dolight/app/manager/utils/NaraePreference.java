package gom.dolight.app.manager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"SameParameterValue", "WeakerAccess", "UnusedParameters"})
@SuppressLint("NewApi")
public class NaraePreference {

    protected static NaraePreference instance;

    public static NaraePreference getInstance(Context c) {
        if (instance == null) {
            instance = new NaraePreference(c);
        }
        return instance;
    }

    public static Context mContext;
    public static String str;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences pref;
    public static ArrayList<String> list;
    public static char[] target;
    public static ArrayList<String> keylist = new ArrayList<>();

    public NaraePreference(Context c) {
        mContext = c;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        editor.apply();
    }

    public ArrayList<String> getKeylist() {
        return getKeylist();
    }

    public void put(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, CharSequence value) {
        editor.putString(key, String.valueOf(value));
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, double value) {
        editor.putString(key, String.valueOf(value));
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, char value) {
        editor.putString(key, String.valueOf(value));
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, BigInteger value) {
        editor.putString(key, Arrays.toString(value.toByteArray()));
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, Byte[] value) {
        editor.putString(key, Arrays.toString(value));
        editor.commit();
        keylist.add(key);
    }

    @SuppressLint("NewApi")
    public void put(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, Object value) {
        editor.putString(key, (String) value);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, HashMap<String, String> value) {
        for (String s : value.keySet()) {
            editor.putString(s, value.get(s));
        }
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, ArrayList<String> value) {
        Set<String> list = new HashSet<>(value);
        editor.putStringSet(key, list);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, String[] value) {
        ArrayList<String> arraylist = new ArrayList<>(Arrays.asList(value));
        Set<String> list = new HashSet<>(arraylist);
        editor.putStringSet(key, list);
        editor.commit();
        keylist.add(key);
    }

    public void put(String key, char[] value) {
        List<Character> chars = new ArrayList<>();
        for (char aValue : value) {
            chars.add(aValue);
        }
        for (Character aChar : chars) {
            list.add(String.valueOf(aChar));
        }
        Set<String> setlist = new HashSet<>(list);
        editor.putStringSet(key, setlist);
        editor.commit();
        keylist.add(key);
    }

    /**
     * @deprecated Use put(String key, char[] value) that add API 11
     */
    public void put_deprecated(String key, char[] value) {
        List<Character> chars = new ArrayList<>();
        for (char aValue : value) {
            chars.add(aValue);
        }
        for (Character aChar : chars) {
            list.add(String.valueOf(aChar));
        }
        JSONArray a = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            a.put(list.get(i));
        }
        if (!list.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
        keylist.add(key);
    }

    /**
     * @deprecated Use put(String key, ArrayList<String> value) that add API 11
     */
    public void put_deprecated(String key, String[] value) {
        ArrayList<String> arraylist = new ArrayList<>(Arrays.asList(value));
        JSONArray a = new JSONArray();
        for (int i = 0; i < arraylist.size(); i++) {
            a.put(arraylist.get(i));
        }
        if (!arraylist.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
        keylist.add(key);
    }

    /**
     * @deprecated Use put(String key, ArrayList<String> value) that add API 11
     */
    public void put_deprecated(String key, ArrayList<String> value) {
        JSONArray a = new JSONArray();
        for (int i = 0; i < value.size(); i++) {
            a.put(value.get(i));
        }
        if (!value.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
        keylist.add(key);
    }

    public ArrayList<String> getValue(String key, ArrayList<String> defaultvalue) {
        try {
            return new ArrayList<>(pref.getStringSet(key, null));
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public String[] getValue(String key, String[] defaultvalue) {
        try {
            Set<String> stringSet = pref.getStringSet(key, null);
            return stringSet.toArray(new String[stringSet.size()]);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    /**
     * @deprecated Use getValue(String key, char[] value) that add API 11
     */
    public char[] getValue_deprecated(String key, char[] defaultvalue) {
        try {
            String json = pref.getString(key, null);
            ArrayList<String> urls = new ArrayList<>();
            if (json != null) {
                try {
                    JSONArray a = new JSONArray(json);
                    for (int i = 0; i < a.length(); i++) {
                        String url = a.optString(i);
                        urls.add(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String[] stringlist = (String[]) urls.toArray();
            for (int i = 0; i < stringlist.length; i++) {
                target[i] = stringlist[i].charAt(i);
            }
            return target;
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public char[] getValue(String key, char[] defaultvalue) {
        try {
            ArrayList<String> arraylist = new ArrayList<>(pref.getStringSet(key, null));
            String[] stringlist = (String[]) arraylist.toArray();
            for (int i = 0; i < stringlist.length; i++) {
                target[i] = stringlist[i].charAt(i);
            }
            return target;
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    /**
     * @deprecated Use getValue(String key, String[] value) that add API 11
     */
    public String[] getValue_deprecated(String key, String[] defaultvalue) {
        try {
            String json = pref.getString(key, null);
            ArrayList<String> urls = new ArrayList<>();
            if (json != null) {
                try {
                    JSONArray a = new JSONArray(json);
                    for (int i = 0; i < a.length(); i++) {
                        String url = a.optString(i);
                        urls.add(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return urls.toArray(new String[urls.size()]);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    /**
     * @deprecated Use getValue(String key, ArrayList<String> value) that add
     * API 11
     */
    public ArrayList<String> getValue_deprecated(String key, ArrayList<String> defaultvalue) {
        try {
            String json = pref.getString(key, null);
            ArrayList<String> urls = new ArrayList<>();
            if (json != null) {
                try {
                    JSONArray a = new JSONArray(json);
                    for (int i = 0; i < a.length(); i++) {
                        String url = a.optString(i);
                        urls.add(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return urls;
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getValue(String key, HashMap<String, String> defaultvalue) {
        try {
            HashMap<String, String> map = (HashMap<String, String>) pref.getAll();
            for (String s : map.keySet()) {
                String value = map.get(s);
                map.put(s, value);
            }
            return map;
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public Set<String> getValue(String key, Set<String> defaultvalue) {
        try {
            return pref.getStringSet(key, defaultvalue);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public String getValue(String key, String defaultvalue) {
        try {
            return pref.getString(key, defaultvalue);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public char getValue(String key, char defaultvalue) {
        try {
            String s = pref.getString(key, String.valueOf(defaultvalue));
            return s.charAt(0);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public CharSequence getValue(String key, CharSequence defaultvalue) {
        try {
            return pref.getString(key, String.valueOf(defaultvalue));
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public BigInteger getValue(String key, BigInteger defaultvalue) {
        try {
            long str = pref.getLong(key, 0);
            return BigInteger.valueOf(str);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public byte[] getValue(String key, byte[] defaultvalue) {
        try {
            str = pref.getString(key, Arrays.toString(defaultvalue));
            return str.getBytes();
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public int getValue(String key, int defaultvalue) {
        try {
            return pref.getInt(key, defaultvalue);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public boolean getValue(String key, boolean defaultvalue) {
        try {
            return pref.getBoolean(key, defaultvalue);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public long getValue(String key, long defaultvalue) {
        try {
            return pref.getLong(key, defaultvalue);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public float getValue(String key, float defaultvalue) {
        try {
            return pref.getFloat(key, defaultvalue);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public double getValue(String key, double defaultvalue) {
        try {
            str = pref.getString(key, String.valueOf(defaultvalue));
            return Double.parseDouble(str);
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public Object getValue(String key, Object defaultvalue) {
        try {
            return str = pref.getString(key, String.valueOf(defaultvalue));
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    public void delete(String key) {
        editor.remove(key);
        editor.commit();
        if (keylist.contains(key))
            keylist.remove(keylist.indexOf(key));
    }

    public void clear() {
        editor.clear();
        editor.commit();
        keylist.clear();;
    }
}
