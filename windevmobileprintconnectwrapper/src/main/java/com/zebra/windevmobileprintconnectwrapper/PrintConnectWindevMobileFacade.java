package com.zebra.windevmobileprintconnectwrapper;

// Android / Java imports
import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import java.util.HashMap;

// PrintConnectWrapper Imports
import com.zebra.printconnectintentswrapper.*;
import com.zebra.printconnectintentswrapperenums.PC_E_FILEMODE;

public class PrintConnectWindevMobileFacade {

    public interface IAppelProcedureWL
    {
        void appelProcedureWLS(String param1);
        void appelProcedureWLSS(String param1, String param2);
        void appelProcedureWLSSS(String param1, String param2, String param3);
    }

    public interface IActivityRetriever
    {
        Activity getActivity();
    }

    // Membres
    private String TAG = "PrintConnectWindevMobileFacade";

    // Interface pour executer les procedures WL
    // Cet objet doit être implémenté dans la collection de procedures WL
    private IAppelProcedureWL mAppelProcedureWL = null;

    // Interface pour récupérer l'activité courante de l'application
    // Cet objet doit être implémenté dans la collection de procédures WL
    private IActivityRetriever mActivityRetriever = null;
    
    private long mlTimeoutMs = 10000;

    public PrintConnectWindevMobileFacade(IAppelProcedureWL aAppelProcedureWLInterface, IActivityRetriever aActivityRetrieverInterface)
    {
        mAppelProcedureWL = aAppelProcedureWLInterface;
        mActivityRetriever = aActivityRetrieverInterface;
    }

    private Activity getActivity()
    {
        if(mActivityRetriever != null)
        {
            return mActivityRetriever.getActivity();
        }
        return null;
    }

    public void setTimeOut(final long flTimeoutMs)
    {
        mlTimeoutMs = flTimeoutMs;
    }

    public long getTimeOut()
    {
        return mlTimeoutMs;
    }
    
    private void logMessage(String message)
    {
        Log.d(TAG, message);
    }
    
    public void PrintConnectStatusImprimante(final String fsCallbackSucces, final String fsCallbackError)
    {
        PCPrinterStatus printerStatus = new PCPrinterStatus(getActivity());
        PCIntentsBaseSettings settings = new PCIntentsBaseSettings()
        {{
            mTimeOutMS = mlTimeoutMs;
            mCommandId = "printerstatus";
        }};

        printerStatus.execute(settings, new PCPrinterStatus.onPrinterStatusResult() {
            @Override
            public void success(PCIntentsBaseSettings settings, HashMap<String, String> printerStatusMap) {
                logMessage("Status récupéré avec succès");
                String statusReturn = "";
                for (HashMap.Entry<String, String> entry : printerStatusMap.entrySet()) {
                    statusReturn += entry.getKey() + " = " + entry.getValue() + "\n";
                }
                if(fsCallbackSucces != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, statusReturn);
                    }
                }
            }

            @Override
            public void error(String errorMessage, int resultCode, Bundle resultData, PCIntentsBaseSettings settings) {
                String sErreur = "Une erreur s'est produite lors de la récupération du status";
                logMessage(sErreur);
                logMessage("Erreur:" + errorMessage);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, errorMessage);
                    }
                }
            }

            @Override
            public void timeOut(PCIntentsBaseSettings settings) {
                String sErreur = "Timeout lors de la récupération du status";
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
            }
        });
    }

    public void PrintConnectImprimeLigne(final String fsLigneAImprimer, final String fsCallbackSucces, final String fsCallbackError)
    {
        PCLinePrintPassthroughPrint linePrintPassthroughPrint = new PCLinePrintPassthroughPrint(getActivity());

        PCLinePrintPassthroughPrintSettings settings = new PCLinePrintPassthroughPrintSettings()
        {{
            mTimeOutMS = mlTimeoutMs;
            mLineToPrint = fsLigneAImprimer;
        }};

        linePrintPassthroughPrint.execute(settings, new PCLinePrintPassthroughPrint.onLinePrintPassthroughResult() {
            @Override
            public void success(PCLinePrintPassthroughPrintSettings settings) {
                logMessage("Ligne imprimée avec succès:" + fsLigneAImprimer);
                if(fsCallbackSucces != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, fsLigneAImprimer);
                    }
                }
            }

            @Override
            public void error(String errorMessage, int resultCode, Bundle resultData, PCLinePrintPassthroughPrintSettings settings) {
                String sErreur = "Une erreur s'est produite lors de l\\'impression de la ligne: " + fsLigneAImprimer;
                logMessage(sErreur);
                logMessage("Erreur:" + errorMessage);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, errorMessage);
                    }
                }
            }

            @Override
            public void timeOut(PCLinePrintPassthroughPrintSettings settings) {
                String sErreur = "Timeout lors de l\\'impression de la ligne: " + fsLigneAImprimer;
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
            }
        });
    }

    public void PrintConnectDeselectionneImprimante(final String fsCallbackSucces, final String fsCallbackError)
    {
        PCUnselectPrinter unselectPrinter = new PCUnselectPrinter(getActivity());
        PCIntentsBaseSettings settings = new PCIntentsBaseSettings()
        {{
            mTimeOutMS = mlTimeoutMs;
            mCommandId = "unselectPrinter";
        }};

        logMessage("Déselection de l'imprimante.");
        unselectPrinter.execute(settings, new PCUnselectPrinter.onUnselectPrinterResult() {
            @Override
            public void success(PCIntentsBaseSettings settings) {
                logMessage("Imprimante désélectionnée avec succès");
                logMessage("Tentative d'appel de la procédure:" + fsCallbackSucces);
                if(fsCallbackSucces != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLS(fsCallbackSucces);
                    }
                }
            }

            @Override
            public void error(String errorMessage, int resultCode, Bundle resultData, PCIntentsBaseSettings settings) {
                String sErreur = "Une erreur s'est produite lors de la désélection de l\\'imprimante";
                logMessage(sErreur);
                logMessage("Erreur:" + errorMessage);
                logMessage("Tentative d'appel de la procédure:" + fsCallbackError);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, errorMessage);
                    }
                }
            }

            @Override
            public void timeOut(PCIntentsBaseSettings settings) {
                String sErreur = "Timeout lors de la désélectiond de l\\'imprimante";
                logMessage(sErreur);
                logMessage("Tentative d'appel de la procédure:" + fsCallbackError);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
            }
        });
    }

    public void PrintConnectImprimeTexte(final String texteAImprimer, final String fsCallbackSucces, final String fsCallbackError)
    {
        PCPassthroughPrint passthroughPrint = new PCPassthroughPrint(getActivity());

        PCPassthroughPrintSettings settings = new PCPassthroughPrintSettings()
        {{
            mPassthroughData = texteAImprimer;
            mTimeOutMS = mlTimeoutMs;
        }};

        passthroughPrint.execute(settings, new PCPassthroughPrint.onPassthroughResult() {
            @Override
            public void success(PCPassthroughPrintSettings settings) {
                logMessage("Chaine ZPL imprimée avec succès:" + texteAImprimer);
                if(fsCallbackSucces != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, texteAImprimer);
                    }
                }
            }

            @Override
            public void error(String errorMessage, int resultCode, Bundle resultData, PCPassthroughPrintSettings settings) {
                String sErreur = "Une erreur s'est produite lors de l\\'impression de la chaine ZPL: " + texteAImprimer;
                logMessage(sErreur);
                logMessage("Erreur:" + errorMessage);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, errorMessage);
                    }
                }
            }

            @Override
            public void timeOut(PCPassthroughPrintSettings settings) {
                String sErreur = "Timeout lors de l\\'impression de la chaine ZPL: " + texteAImprimer;
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
            }
        });
    }

    public void PrintConnectImprimeFichierTemplateVariable(final String fsFichierAImprimer, final String fsFileMode, final String fsVariableDataKeysCommaSeparatedString, final String fsVariableDataValuesCommaSeparatedString, final String fsCallbackSucces, final String fsCallbackError)
    {
        HashMap<String, String> variableData = null;

        if(fsVariableDataKeysCommaSeparatedString != null && fsVariableDataKeysCommaSeparatedString.isEmpty() == false)
        {
            String[] variableDataKeys = fsVariableDataKeysCommaSeparatedString.split(";");
            String[] variableDataValues = fsVariableDataValuesCommaSeparatedString.split(";");
            variableData = new HashMap<String, String>();
            if(variableDataKeys.length != variableDataValues.length)
            {
                String sErreur = "Taille des clés et valeurs différentes dans ZebraImprimeFichierTemplateVariable";
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
                return;
            }
            for(int i = 0; i < variableDataKeys.length; i++)
            {
                logMessage("key:" + variableDataKeys[i] + " | value: " + variableDataValues[i]);
                variableData.put(variableDataKeys[i], variableDataValues[i]);
            }
        }

        final HashMap<String, String> fVariableData = variableData;

        final PC_E_FILEMODE feFileMode = PC_E_FILEMODE.getFileMode(fsFileMode);

        PCTemplateFileNamePrint templateFileNamePrint = new PCTemplateFileNamePrint(getActivity());

        PCTemplateFileNamePrintSettings settings = new PCTemplateFileNamePrintSettings()
        {{
            mTimeOutMS = mlTimeoutMs;
            mTemplateFileName = fsFichierAImprimer;
            mVariableData = fVariableData;
            mFileMode = feFileMode;
        }};

        templateFileNamePrint.execute(settings, new PCTemplateFileNamePrint.onPrintFileNameResult() {
            @Override
            public void success(PCTemplateFileNamePrintSettings settings) {
                logMessage("Fichier ZPL variable imprimé avec succès:" + fsFichierAImprimer);
                if(fsCallbackSucces != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, fsFichierAImprimer);
                    }
                }
            }

            @Override
            public void error(String errorMessage, int resultCode, Bundle resultData, PCTemplateFileNamePrintSettings settings) {
                String sErreur = "Une erreur s'est produite lors de l\\'impression du fichier ZPL variable: " + fsFichierAImprimer;
                logMessage(sErreur);
                logMessage("Erreur:" + errorMessage);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, errorMessage);
                    }
                }
            }

            @Override
            public void timeOut(PCTemplateFileNamePrintSettings settings) {
                String sErreur = "Timeout lors de l\\'impression du fichier ZPL variable: " + fsFichierAImprimer;
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
            }
        });
    }

    public void PrintConnectImprimeChaineZPLVariable(final String chaineZplAImprimer, final String variableDataKeysCommaSeparatedString, final String variableDataValuesCommaSeparatedString, final String fsCallbackSucces, final String fsCallbackError)
    {
        HashMap<String, String> variableData = null;

        if(variableDataKeysCommaSeparatedString != null && variableDataKeysCommaSeparatedString.isEmpty() == false)
        {
            String[] variableDataKeys = variableDataKeysCommaSeparatedString.split(";");
            String[] variableDataValues = variableDataValuesCommaSeparatedString.split(";");
            variableData = new HashMap<String, String>();
            if(variableDataKeys.length != variableDataValues.length)
            {
                String sErreur = "Taille des clés et valeurs différentes dans ZebraPCImprimeChaineZPLVariable";
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
                return;
            }
            for(int i = 0; i < variableDataKeys.length; i++)
            {
                logMessage("key:" + variableDataKeys[i] + " | value: " + variableDataValues[i]);
                variableData.put(variableDataKeys[i], variableDataValues[i]);
            }
        }

        final HashMap<String, String> fVariableData = variableData;

        PCTemplateStringPrint templateStringPrint = new PCTemplateStringPrint(getActivity());

        PCTemplateStringPrintSettings settings = new PCTemplateStringPrintSettings()
        {{
            mTimeOutMS = mlTimeoutMs;
            mZPLTemplateString = chaineZplAImprimer;
            mVariableData = fVariableData;
        }};

        templateStringPrint.execute(settings, new PCTemplateStringPrint.onPrintTemplateStringResult() {
            @Override
            public void success(PCTemplateStringPrintSettings settings) {
                logMessage("Chaine ZPL variable imprimée avec succès:" + chaineZplAImprimer);
                if(fsCallbackSucces != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, chaineZplAImprimer);
                    }
                }
            }

            @Override
            public void error(String errorMessage, int resultCode, Bundle resultData, PCTemplateStringPrintSettings settings) {
                String sErreur = "Une erreur s'est produite lors de l\\'impression de la chaine ZPL variable: " + chaineZplAImprimer;
                logMessage(sErreur);
                logMessage("Erreur:" + errorMessage);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, errorMessage);
                    }
                }
            }

            @Override
            public void timeOut(PCTemplateStringPrintSettings settings) {
                String sErreur = "Timeout lors de l\\'impression de la chaine ZPL variable: " + chaineZplAImprimer;
                logMessage(sErreur);
                if(fsCallbackError != "")
                {
                    if(mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSS(fsCallbackError, sErreur, "");
                    }
                }
            }
        });
    }

}
