package com.coolbitx.coolwallet.general;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coolbitx.coolwallet.DataBase.DatabaseHelper;
import com.coolbitx.coolwallet.R;
import com.coolbitx.coolwallet.bean.Account;
import com.coolbitx.coolwallet.bean.Card;
import com.coolbitx.coolwallet.bean.Constant;
import com.coolbitx.coolwallet.bean.CwBtcTxs;
import com.coolbitx.coolwallet.bean.ExchangeOrder;
import com.coolbitx.coolwallet.bean.ExchangeRate;
import com.coolbitx.coolwallet.bean.Host;
import com.coolbitx.coolwallet.bean.Info;
import com.coolbitx.coolwallet.bean.NodeUnSpentTxsBean;
import com.coolbitx.coolwallet.bean.ParsingAddress;
import com.coolbitx.coolwallet.bean.PasingWallet;
import com.coolbitx.coolwallet.bean.Txs;
import com.coolbitx.coolwallet.bean.UnSpentTxsBean;
import com.coolbitx.coolwallet.bean.User;
import com.coolbitx.coolwallet.bean.Wallet;
import com.coolbitx.coolwallet.bean.socketByAddress;
import com.coolbitx.coolwallet.ui.BleActivity;
import com.coolbitx.coolwallet.util.ByteUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.snscity.egdwlib.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;


/**
 * Created by MyPC on 2015/8/27.
 * public function
 */
public class PublicPun {

    public final static String csvFilename = "Login";
    public final static String oldPin = "12345678";
    public final static String newPin = "12345678";
    public final static String default_oldPin = "123456";
    public final static String default_newPin = "12345678";
    public static final int HANDLER_SEND_BTC_FINISH = 9527;
    public static final int HANDLER_SEND_BTC_ERROR = 9521;
    /**
     * 卡片的工作模式
     */
    public static String modeState = "";

    /**
     * 卡片的版本号
     */
    public static String fwVersion = "";

    /**
     * 卡片的唯一标识符
     */
    public static String uid = "";

    public static String cardId = "";

    public static Card card = new Card();

    public static String cardName = "";

    public static HdwInfoBean hdwInfoBean = null;

    public static User user = new User();

    public static List<Host> hostList = new ArrayList<>();


    public static boolean[] accountSocketReg = new boolean[5];

    public static boolean[] accountRefresh = new boolean[5];

//    public static Host[] hostList = new Host[3];

    public static Wallet wallet = new Wallet();

    /**
     * 管理fragment栈
     */
//    public static List<Fragment> backStackList = new ArrayList<>();

    /**
     * Encryption key
     */
    public static byte[] BIND_SENCK = new byte[16];

    /** */
    public static byte[] BIND_SMACK = new byte[16];

    public static List<HostBean> hostBeanList = new ArrayList<>();

    public static Account account = new Account();

    //Blockchain data
    static ArrayList<ExchangeRate> lisExchangeRate = new ArrayList<ExchangeRate>();
    public static double SATOSHI_RATE = 0.00000001;


    public static int FindScreenSize(Context mContext) {
        //Find screen size
        WindowManager manager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;
        return smallerDimension;
    }


    public static byte[] hexStringToByteArray(String s) {
        byte[] data = null;
        try {
            int len = s.length();
            data = new byte[len / 2];

            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {

        }
        return data;
    }

    public static byte[] hexStringToByteArrayRev(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static String jSonGen(String address) {
        String mResult = null;
        //準備資料
        String strNetwork = "BTC";
        String type = "address";

        //開始拼接字串
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"network\":\"" + strNetwork + "\",");
        sb.append("\"type\":\"" + type + "\",");
        sb.append("\"address\":\"" + address + "\"");
        sb.append("}");
        mResult = sb.toString();

        return mResult;
    }

    public static String[] jsonParserXchsPrepare(String jsonString) {

        String[] perpTrxResult = null;

        ArrayList<ExchangeOrder> listExchangeOrder = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonArrayBlk = jsonObject.getJSONArray("blks");

            perpTrxResult = new String[jsonArrayBlk.length()];
            for (int i = 0; i < jsonArrayBlk.length(); i++) {
                JSONObject jsonObjectData = jsonArrayBlk.getJSONObject(i);
                perpTrxResult[i] = jsonObjectData.getString("blk");
                LogUtil.e("jsonParserXchsPrepare " + perpTrxResult[i]);
            }
        } catch (Exception e) {
            LogUtil.e("jsonParserXchsPrepare 錯誤:" + e.toString());
            e.printStackTrace();
        } finally {

            return perpTrxResult;
        }
    }

    public static ArrayList<ExchangeOrder> jsonParserExchange(String jsonString, String mType, boolean orderBy) {

        ArrayList<ExchangeOrder> listExchangeOrder = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Gson gson = new Gson();

            JSONArray jsonArrayOrder = jsonObject.getJSONArray(mType);

            for (int i = 0; i < jsonArrayOrder.length(); i++) {
                JSONObject jsonObjectSellData = jsonArrayOrder.getJSONObject(i);
                ExchangeOrder exchangeOrder = gson.fromJson(jsonObjectSellData.toString(), ExchangeOrder.class);
                exchangeOrder.setType(mType);
                LogUtil.i("exchangeOrder" + String.valueOf(i) + " 筆=type:" + exchangeOrder.getType() + " , orderId:" +
                        exchangeOrder.getOrderId() + " , cwOrderId:" + exchangeOrder.getCworderId() + " , " + exchangeOrder.getAddr() + " , " +
                        String.valueOf(exchangeOrder.getAmount()) + " , " + exchangeOrder.getAccount() + " , " +
                        exchangeOrder.getPrice() + " , " + exchangeOrder.getExpiration() + " , isSubmitted:" + exchangeOrder.isSubmitted());
                // 15847930 , null , 50 , 3 , 234.56 , null

                listExchangeOrder.add(exchangeOrder);

                if (orderBy) {
                    Collections.sort(listExchangeOrder, new Comparator<ExchangeOrder>() {

                        @Override
                        public int compare(ExchangeOrder lhs, ExchangeOrder rhs) {
                            return lhs.getExpiration().compareTo(rhs.getExpiration());
                        }
                    });
                }

            }
        } catch (Exception e) {
            LogUtil.e("JSONException:" + e.toString());
            e.printStackTrace();
        } finally {

            return listExchangeOrder;
        }
    }

    private static String getAddressFromScript(String script) {
        byte[] bareAddress = new byte[20];
        //(來源陣列，起始索引值，目的陣列，起始索引值，複製長度)
//        System.arraycopy( PublicPun.hexStringToByteArray("76a914934abe98a533cab0946a85d3bad409778a077c7088ac"),3,bareAddress, 0, bareAddress.length);
        System.arraycopy(PublicPun.hexStringToByteArray(script), 3, bareAddress, 0, bareAddress.length);

        byte[] baddr = new byte[bareAddress.length + 1];
        baddr[0] = 0;
        System.arraycopy(bareAddress, 0, baddr, 1, bareAddress.length);

        return ByteUtils.toBase58WithChecksum(baddr);
    }

    public static socketByAddress jsonParserSocketAddress(String jsonString) {
        socketByAddress socketAddress = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //建立Gson類別並將JSON資料裝入class物件裡
            Gson gson = new Gson();
            System.out.print("jsonParserSocketAddress:" + jsonObject.get("type"));
            if (jsonObject.get("type").equals("address")) {
                socketAddress = new socketByAddress();
                JSONObject jsonObjectSocket = jsonObject.getJSONObject("data");
                socketAddress = gson.fromJson(jsonObjectSocket.toString(), socketByAddress.class);
                if (Double.valueOf(socketAddress.getAmount_received()) > 0) {
                    socketAddress.setTx_type("Received");
                    socketAddress.setBtc_amount(Double.valueOf(socketAddress.getAmount_received()));
                } else {
                    socketAddress.setTx_type("Sent");
                    socketAddress.setBtc_amount(Double.valueOf(socketAddress.getAmount_sent()));
                }
            } else {
                socketAddress = null;

            }
        } catch (Exception e) {
//            LogUtil.e("recovery GsonEception:" + e.toString());
            e.printStackTrace();
            socketAddress = null;
        } finally {

            return socketAddress;
        }
    }

    public static boolean jsonParsingFeeaRate(Context mContext, String jsonString) {
        boolean mResult = false;
        try {
            System.out.print("jsonParsingFeeRate=" + jsonString);
            JSONObject jsonObjectFeesRate = new JSONObject(jsonString);

            AppPrefrence.saveRecommendedFastestFee(mContext, jsonObjectFeesRate.getInt("fastestFee"));
            AppPrefrence.saveRecommendedHalfHourFees(mContext, jsonObjectFeesRate.getInt("halfHourFee"));
            AppPrefrence.saveRecommendedHourFee(mContext, jsonObjectFeesRate.getInt("hourFee"));

            LogUtil.d("fastestFee=" + AppPrefrence.getRecommendedFastestFee(mContext) + ";halfHourFee=" +
                    AppPrefrence.getRecommendedHalfHourFees(mContext) + ";hourFee=" +
                    AppPrefrence.getRecommendedHourFee(mContext));

            mResult = true;
        } catch (Exception e) {
            mResult = false;
            LogUtil.e("GsonEception:" + e.toString());
            e.printStackTrace();
        } finally {
            return mResult;
        }

    }

    public static boolean jsonParserBlockChainRate(Context mContext, String jsonString) {

        boolean mResult = false;
        try {
            JSONObject jsonObjectExchangeRate = new JSONObject(jsonString);
            for (int j = 0; j < jsonObjectExchangeRate.names().length(); j++) {
//                    String strRates = jsonObjectExchangeRate.get(jsonObjectExchangeRate.names().getString(j)).toString();
                Object objList = jsonObjectExchangeRate.getString(jsonObjectExchangeRate.names().getString(j));
                JSONObject jsonObjList = new JSONObject(objList.toString());
                String strRates = jsonObjList.get("last").toString();
                double rates = Double.parseDouble(strRates);
                String key = jsonObjectExchangeRate.names().getString(j);
//                    LogUtil.d("匯率: " + key + " value = " + rates);
                DatabaseHelper.insertCurrent(mContext, key, rates);
            }
            mResult = true;
        } catch (Exception e) {
            mResult = false;
            LogUtil.e("GsonEception:" + e.toString());
            e.printStackTrace();
        } finally {
            return mResult;
        }
    }


    public static ArrayList<UnSpentTxsBean> jsonParseBlockChainInfoUnspent(String jsonString, boolean isNode) throws JSONException {

        ArrayList<UnSpentTxsBean> lisUnSpentTxs = new ArrayList<UnSpentTxsBean>();
        Object jsonData;
        JSONArray jsonDataJsonArray;

//        if (jsonData instanceof JSONArray) {
        //    }else{
//            jsonData instanceof JSONObject
//        }

        if (isNode) {
            jsonDataJsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonDataJsonArray.length(); i++) {
                JSONObject jsonObjectUnspentData = jsonDataJsonArray.getJSONObject(i);

                UnSpentTxsBean mUnSpentTxsBean = new UnSpentTxsBean();
                mUnSpentTxsBean.setTx(jsonObjectUnspentData.getString("txid"));
                mUnSpentTxsBean.setAmount(jsonObjectUnspentData.getLong("satoshis") * SATOSHI_RATE);
                mUnSpentTxsBean.setN(jsonObjectUnspentData.getInt("vout"));
                mUnSpentTxsBean.setConfirmations(jsonObjectUnspentData.getInt("confirmations"));

                mUnSpentTxsBean.setAddress(getAddressFromScript(jsonObjectUnspentData.getString("scriptPubKey")));
                mUnSpentTxsBean.setScript(jsonObjectUnspentData.getString("scriptPubKey"));
                LogUtil.d("address=" + mUnSpentTxsBean.getAddress() + ";value:" + new DecimalFormat("#.########").format(mUnSpentTxsBean.getAmount()) +
                        ";N=" + mUnSpentTxsBean.getN() + ";confirm=" + mUnSpentTxsBean.getConfirmations() + ";script=" + mUnSpentTxsBean.getScript() + "tx=" + mUnSpentTxsBean.getTx()
                );

                if (mUnSpentTxsBean.getConfirmations() != 0) {
                    lisUnSpentTxs.add(mUnSpentTxsBean);
                }
            }

        } else {
            jsonData = new JSONObject(jsonString).get("unspent_outputs");
            jsonDataJsonArray = (JSONArray) jsonData;

            for (int i = 0; i < jsonDataJsonArray.length(); i++) {
                JSONObject jsonObjectUnspentData = jsonDataJsonArray.getJSONObject(i);

                UnSpentTxsBean mUnSpentTxsBean = new UnSpentTxsBean();
                mUnSpentTxsBean.setTx(jsonObjectUnspentData.getString("tx_hash_big_endian"));
                mUnSpentTxsBean.setAmount(jsonObjectUnspentData.getLong("value") * SATOSHI_RATE);
                mUnSpentTxsBean.setN(jsonObjectUnspentData.getInt("tx_output_n"));
                mUnSpentTxsBean.setConfirmations(jsonObjectUnspentData.getInt("confirmations"));
                mUnSpentTxsBean.setAddress(getAddressFromScript(jsonObjectUnspentData.getString("script")));
                mUnSpentTxsBean.setScript(jsonObjectUnspentData.getString("script"));
                LogUtil.d("address=" + mUnSpentTxsBean.getAddress() + ";value:" + new DecimalFormat("#.########").format(mUnSpentTxsBean.getAmount()) +
                        ";N=" + mUnSpentTxsBean.getN() + ";confirm=" + mUnSpentTxsBean.getConfirmations() + ";script=" + mUnSpentTxsBean.getScript() + "tx=" + mUnSpentTxsBean.getTx()
                );

                if (mUnSpentTxsBean.getConfirmations() != 0) {
                    lisUnSpentTxs.add(mUnSpentTxsBean);
                }
            }
        }

        //put the amount in descending order
        Collections.sort(lisUnSpentTxs, new Comparator<UnSpentTxsBean>() {
            @Override
            public int compare(UnSpentTxsBean lhs, UnSpentTxsBean rhs) {
//                    return (int)(lhs.getAmount()*100000000)-(int)(rhs.getAmount()*100000000);//ASC order
                return ((int) (lhs.getAmount() * 100000000) < (int) (rhs.getAmount() * 100000000) ? 1 :
                        ((int) (lhs.getAmount() * 100000000) == (int) (rhs.getAmount() * 100000000) ? 0 : -1));
            }
        });

        return lisUnSpentTxs;

    }


    public static int jsonParserRefresh(Context mContext, String jsonString, int accountID, boolean isAddressesUpdate, boolean isNode) throws JSONException {

        int wallet_txs_cnt = 0;
        String mTxAddr = "";
        long mResult = 0;
        String mDate = "";
        String mTransationID = "";
        String mAddr = "";
        String wid = PublicPun.card.getCardId();
        long mBalance = 0;
        int address_N_tx = 0;
        int latest_block = 0;
        int block_height = 0;
        int confirmations = 0;
//        ArrayList<ParsingAddress> lisAddress = new ArrayList<ParsingAddress>();

        JSONObject jsonObject = new JSONObject(jsonString);
        //建立Gson類別並將JSON資料裝入class物件裡
        Gson gson = new Gson();

        if (isNode) {
            JSONArray jsonDataJsonArray;
            jsonDataJsonArray = jsonObject.optJSONArray("items");
            for (int i = 0; i < jsonDataJsonArray.length(); i++) {
                JSONObject jsonObjectUnspentData = jsonDataJsonArray.getJSONObject(i);
                LogUtil.e("parsing tx:" + jsonObjectUnspentData.toString());

                NodeUnSpentTxsBean mNodeUnSpentTxsBean = gson.fromJson(jsonObjectUnspentData.toString(), NodeUnSpentTxsBean.class);
                LogUtil.e("txid=" + mNodeUnSpentTxsBean.getTxid() + ";confirm=" + mNodeUnSpentTxsBean.getConfirmations());

                JSONArray jsonArrayVin = jsonObjectUnspentData.optJSONArray("vin");
                for (int j = 0; j < jsonArrayVin.length(); j++) {
                    NodeUnSpentTxsBean.vin vin = gson.fromJson(jsonArrayVin.get(j).toString(), NodeUnSpentTxsBean.vin.class);
                    LogUtil.e("看這裡vin:addr=" + vin.getAddr() + ";n=" + vin.getN() + ";value=" + vin.getValue());
                }


                JSONArray jsonArrayVout = jsonObjectUnspentData.optJSONArray("vout");
                for (int j = 0; j < jsonArrayVin.length(); j++) {
                    LogUtil.d(jsonArrayVin.get(j).toString());
                    NodeUnSpentTxsBean.vout vout = gson.fromJson(jsonArrayVout.get(j).toString(), NodeUnSpentTxsBean.vout.class);
                    LogUtil.d("vout value=" + vout.getValue() + ";n=" + vout.getN());

                    JSONArray jsonArrayScriptPub = jsonObjectUnspentData.optJSONArray("scriptPubKey");
                    for (int k = 0; k < jsonArrayScriptPub.length(); k++) {
                        LogUtil.d(jsonArrayScriptPub.get(j).toString());
                        NodeUnSpentTxsBean.vout.scriptPubKey script = gson.fromJson(jsonArrayScriptPub.get(j).toString(), NodeUnSpentTxsBean.vout.scriptPubKey.class);
                        LogUtil.d("vout script=" + script.getAddresses() + " / " + script.getHex());
                    }

                }

//                LogUtil.e("vin:" + mNodeUnSpentTxsBean.getListVin().toString());

//                mNodeUnSpentTxsBean.vin[] t;
//
//                for (int j = 0; j < mNodeUnSpentTxsBean.vin[].size(); j++) {
//                    LogUtil.e("vin:" + mNodeUnSpentTxsBean.getListVin().toString());
//                }
//
//                for (int j = 0; j < mNodeUnSpentTxsBean.getListVout().size(); j++) {
//                    LogUtil.e("vout:" + mNodeUnSpentTxsBean.getListVout().toString());
//                }

            }

        } else {
            //Info
            JSONObject jsonObjectInfo = jsonObject.getJSONObject("info");
            Info mInfo = gson.fromJson(jsonObjectInfo.toString(), Info.class);
            latest_block = mInfo.getLatest_block().getHeight();


            //wallet
            JSONObject jsonObjectWallet = jsonObject.getJSONObject("wallet");
            PasingWallet mWallet = gson.fromJson(jsonObjectWallet.toString(), PasingWallet.class);
            wallet_txs_cnt = mWallet.getN_tx();

            if (isAddressesUpdate) {
                //address
                JSONArray jsonArrayAddress = jsonObject.getJSONArray("addresses");
                for (int i = 0; i < jsonArrayAddress.length(); i++) {
                    JSONObject jsonObjectAddress = jsonArrayAddress.getJSONObject(i);
                    ParsingAddress mAddress = gson.fromJson(jsonObjectAddress.toString(), ParsingAddress.class);
                    //有交易變動的才需要update
                    address_N_tx = mAddress.getN_tx();
                    if (address_N_tx > 0) {
                        mAddr = mAddress.getAddress();
                        mBalance = mAddress.getFinal_balance();
                        LogUtil.d("refresh parsing addresses,account=" + accountID + " ;addr=" + mAddr + " ;balance=" + mBalance + " N_tx=" + address_N_tx);
                        DatabaseHelper.updateAddress(mContext, accountID, mAddr, address_N_tx, mBalance);
                    }
                }
            }
            //Txs
            JSONArray jsonArrayTxs = jsonObject.getJSONArray("txs");
            ArrayList<Txs> lisTxs = new ArrayList<Txs>();
            for (int i = 0; i < jsonArrayTxs.length(); i++) {
                JSONObject jsonObjectTxs = jsonArrayTxs.getJSONObject(i);
                Txs mTxs = gson.fromJson(jsonObjectTxs.toString(), Txs.class);
                lisTxs.add(mTxs);

                //設定日期格式
                Date ts = new Date(lisTxs.get(i).getTime() * 1000);
                mDate = new SimpleDateFormat("dd-MMM-yyyy h:mm a").format(ts);

                mResult = lisTxs.get(i).getResult();
                mTransationID = lisTxs.get(i).getHash();
                block_height = lisTxs.get(i).getBlock_height();
                if (block_height == 0) {
                    confirmations = 0;
                } else {
                    confirmations = latest_block - block_height + 1;
                }
                if (mResult > 0) {
                    //RECV BTC
                    mTxAddr = lisTxs.get(i).getInputs()[0].getPrev_out().getAddr();
                } else {
                    //SEND BTC
                    mTxAddr = lisTxs.get(i).getOut()[0].getAddr();
                }
                CwBtcTxs mCwBtcTxs = new CwBtcTxs();
                mCwBtcTxs.setWID(wid);
                mCwBtcTxs.setAccount_ID(accountID);
                mCwBtcTxs.setAddress(mAddr);
                mCwBtcTxs.setTxs_Address(mTxAddr);
                mCwBtcTxs.setTxs_Date(mDate);
                mCwBtcTxs.setTxs_Result(mResult);
                mCwBtcTxs.setTxs_TransationID(mTransationID);
                mCwBtcTxs.setTxs_Confirmation(confirmations);

                DatabaseHelper.insertTxs(mContext, mCwBtcTxs);
            }
        }
//                LogUtil.i("account=" + accountID + " ;txAddr=" + mTxAddr + " ;mDate=" + mDate + " );


        return wallet_txs_cnt;
    }


    public static void jsonParseAddressinfo(Context mContext, String jsonString, ContentValues cv) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        long finalBalance = 0;
        int address_N_tx = 0;
        String addr = cv.getAsString("addresses");
        finalBalance = jsonObject.getLong("balanceSat");
        address_N_tx = jsonObject.getInt("txApperances");
        DatabaseHelper.updateBalance(mContext, addr, address_N_tx, finalBalance);
    }

    /**
     * recovery or new Host , get individual address blockChain info
     * * @param mContext
     *
     * @param jsonString
     * @param cv
     * @param isInsert
     * @return
     */
    public static boolean jsonParserRecoveryAddresses(Context mContext, String jsonString,
                                                      ContentValues cv, boolean isInsert, boolean isNode) throws JSONException {


        int accountId = cv.getAsInteger("ACCOUNT_ID");
        int kcId = cv.getAsByte("KCID");
        int kid = cv.getAsByte("KID");
        String addr = cv.getAsString("addresses");


        long finalBalance = 0;
        int address_N_tx = 0;
        boolean mResult = true;

        JSONObject jsonObject = new JSONObject(jsonString);

        if (isNode) {
            finalBalance = jsonObject.getLong("balanceSat");
            address_N_tx = jsonObject.getInt("txApperances");
        } else {

            //建立Gson類別並將JSON資料裝入class物件裡
            Gson gson = new Gson();
            JSONObject jsonObjectWallet = jsonObject.getJSONObject("wallet");
            PasingWallet mWallet = gson.fromJson(jsonObjectWallet.toString(), PasingWallet.class);
//          存satoshi/顯示的時候再轉換btc單位
            finalBalance = mWallet.getFinal_balance();
            address_N_tx = mWallet.getN_tx();
        }

        if (isInsert) {
            DatabaseHelper.insertAddress(mContext, accountId, addr, kcId, kid, address_N_tx, finalBalance);
        } else {
            DatabaseHelper.updateAddress(mContext, accountId, addr, kcId, kid, address_N_tx, finalBalance);
        }
        if (address_N_tx == 0) {
            //no txs
            mResult = false;
        } else {
            mResult = true;
        }
        return mResult;

    }

    public static byte[] calcKey(byte[] devKey, String s, byte[] loginChallenge) {
        byte[] ss = s.getBytes(Constant.UTF8);
        int length1 = loginChallenge.length;
        int length2 = devKey.length;
        int length3 = ss.length;

        int length = length1 + length2 + length3;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length1; i++) {
            bytes[i] = loginChallenge[i];
        }

        for (int i = 0; i < length2; i++) {
            bytes[i + length1] = devKey[i];
        }

        for (int i = 0; i < length3; i++) {
            bytes[i + length1 + length2] = ss[i];
        }

//        LogUtil.e(PublicPun.printBytes(bytes));
        LogUtil.i("calcKey=" + LogUtil.byte2HexString(bytes));
        return bytes;
    }

    public static float parseStringToFloatInternational(String str) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        Number number = null;
//        try {
        number = format.parse(str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            LogUtil.e("edtFee parsing error! " + e.getMessage());
//        }
        return number.floatValue();
    }


//    public static void showNoticeDialog(Context mContext, String mTitle, String mMessage) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View alert_view = inflater.inflate(R.layout.edit_dialog, null);//alert為另外做給alert用的layout
////        final EditText mEditText = (EditText) alert_view.findViewById(R.id.etInputLabel);
//        final TextView mDialogTitle = (TextView) alert_view.findViewById(R.id.dialog_title);
//        final TextView mDialogMessage = (TextView) alert_view.findViewById(R.id.dialog_message);
////        mEditText.setVisibility(View.INVISIBLE);
//        //-----------產生輸入視窗--------
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//        mDialogTitle.setText(mTitle);
//        mDialogMessage.setText(mMessage);
//        builder.setView(alert_view);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//            }
//        });
//        LogUtil.e("clickFunction");
//        builder.show();
//    }


    public static AlertDialog.Builder CustomNoticeDialog(Context mContext, String mTitle, String mMessage) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alert_view = inflater.inflate(R.layout.edit_dialog, null);//alert為另外做給alert用的layout
        final TextView mDialogTitle = (TextView) alert_view.findViewById(R.id.dialog_title);
        final TextView mDialogMessage = (TextView) alert_view.findViewById(R.id.dialog_message);
        mDialogTitle.setText(mTitle);
        mDialogMessage.setText(mMessage);
        //-----------產生輸入視窗--------

        return new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setView(alert_view);
//                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                    }
//                }).show();


    }


    /**
     * show Dialog Messahe
     **/
    private static Timer mTimer;

    public static void showNoticeDialog(final Context mContext, final String mTitle, final String mMessage) {


        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alert_view = inflater.inflate(R.layout.edit_dialog, null);//alert為另外做給alert用的layout
        final TextView mDialogTitle = (TextView) alert_view.findViewById(R.id.dialog_title);
        final TextView mDialogMessage = (TextView) alert_view.findViewById(R.id.dialog_message);
        mDialogTitle.setText(mTitle);
        mDialogMessage.setText(mMessage);
        //-----------產生輸入視窗--------
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        LogUtil.d("showNoticeDialog");
        if (mContext != null) {
            //show dialog
            LogUtil.e("showNoticeDialog From:" + cn.getShortClassName());
            try {
                new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)//
                        .setView(alert_view)
                        .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();

            } catch (Exception e) {
                LogUtil.e("showNoticeDialog錯誤＝" + e.getMessage());
            }
        }

//        Dialog dialog=builder.create();
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        dialog.show();

    }


    public static void showNoticeDialogToFinish(final Context mContext, String mTitle, String mMessage) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alert_view = inflater.inflate(R.layout.edit_dialog, null);//alert為另外做給alert用的layout
        final EditText mEditText = (EditText) alert_view.findViewById(R.id.etInputLabel);
        final TextView mDialogTitle = (TextView) alert_view.findViewById(R.id.dialog_title);
        final TextView mDialogMessage = (TextView) alert_view.findViewById(R.id.dialog_message);
//        mEditText.setVisibility(View.INVISIBLE);
        //-----------產生輸入視窗--------ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT

        if (!((Activity) mContext).isFinishing()) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setCancelable(false);
                mDialogTitle.setText(mTitle);
                mDialogMessage.setText(mMessage);
                builder.setView(alert_view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        BleActivity.bleManager.disConnectBle();
//                    ((Activity) mContext).finish(); // 離開程式
                        Intent intent = new Intent(mContext, BleActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
                        mContext.startActivity(intent);

                    }
                });
                builder.show();
            } catch (Exception e) {
                LogUtil.e("showNoticeDialogToFinish 錯誤：" + e.getMessage());
            }
        }
    }

    /**
     * 短土司
     *
     * @param context
     * @param text
     */
    public static void toast(Context context, CharSequence text) {
        Toast.makeText(context, (text == null ? "空" : text), Toast.LENGTH_SHORT).show();
    }

    public static String printBytes(byte[] bytes) {
        if (bytes == null) return null;
        StringBuilder sb = new StringBuilder();
//        sb.append("byte[]=" + Arrays.toString(bytes));
//        sb.append("/n");
        sb.append("byte[]_hash=" + byte2HexString(bytes));
        return sb.toString();
        //handle=byte[]=[121, -42, -36, -26]/nbyte[]_hash=79 d6 dc e6
    }

    /**
     * byte数组转哈希字符串
     *
     * @param bytes
     * @return
     */
    public static String byte2HexString(byte[] bytes) {
        if (bytes == null) return null;

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x ", b));
        }

        return sb.toString();
    }

    public static String byte2HexStringNoBlank(byte[] bytes) {
        if (bytes == null) return null;

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String byte2HexString(byte b) {
        return String.format("%02x", b);
    }

    public static byte[] encryptSHA256(byte[] bytes) {
        byte[] digestByte;

        digestByte = encrypt(bytes, "SHA-256");
        LogUtil.e(printBytes(digestByte));
        return digestByte;
    }

    public static byte[] encrypt(byte[] bytes, String type) {
        MessageDigest digest;
        byte[] b = null;
        try {
            digest = MessageDigest.getInstance(type);
            b = digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return b;
    }

    public static String selectMode(String mode) {
        LogUtil.e("Mode=" + mode);
        String s = "unknown mode";
        if (mode.equals("00")) {
            s = "INIT";
        } else if (mode.equals("01")) {
            s = "PERSO";
        } else if (mode.equals("02")) {
            s = "NORMAL";
        } else if (mode.equals("03")) {
            s = "AUTH";
        } else if (mode.equals("04")) {
            s = "LOCK";
        } else if (mode.equals("05")) {
            s = "ERROR";
        } else if (mode.equals("06")) {
            s = "NOHOST";
        } else if (mode.equals("07")) {
            s = "DISCONN";
        }

        return s;
    }

    /**
     * 将十进制转换为指定长度的十六进制字符串
     *
     * @param algorism  int 十进制数字
     * @param maxLength int 转换后的十六进制字符串长度
     * @return String 转换后的十六进制字符串
     */
    public static String algorismToHEXString(int algorism, int maxLength) {
        String result = "";
        result = Integer.toHexString(algorism);

        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return patchHexString(result.toUpperCase(), maxLength);
    }

    /**
     * HEX字符串前补0，主要用于长度位数不足。
     *
     * @param str       String 需要补充长度的十六进制字符串
     * @param maxLength int 补充后十六进制字符串的长度
     * @return 补充结果
     */
    static public String patchHexString(String str, int maxLength) {
        String temp = "";
        for (int i = 0; i < maxLength - str.length(); i++) {
            temp = "0" + temp;
        }
        str = (temp + str).substring(0, maxLength);
        return str;
    }

}
