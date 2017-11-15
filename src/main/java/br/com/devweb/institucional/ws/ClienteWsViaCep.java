package br.com.devweb.institucional.ws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import br.com.devweb.institucional.model.UsuarioEndereco;
import br.com.devweb.intitucional.util.ValidaCepUtil;

/**
 * Classe para recuperar informações do WS do viacep.com.br
 */
public class ClienteWsViaCep {

    @SuppressWarnings("unused")
	private static final Set<String> CAMPOS = new HashSet<String>(Arrays.asList(
    "cep",
    "logradouro",
    "complemento",
    "bairro",
    "localidade",
    "uf",
    "unidade",
    "ibge",
    "gia"
    ));

    /**
     * Recupera objeto Endereco pelo CEP
     * @param cep String no formato 00000000 8digitos
     * @return instancia de br.com.viacep.Endereco
     */
    public static UsuarioEndereco getEnderecoPorCep(String cep) {

        JsonObject jsonObject = getCepResponse(cep);
        UsuarioEndereco endereco = null;

        JsonValue erro = jsonObject.get("erro");

        if (erro == null) {

        	// design pattern builder
            endereco = new UsuarioEndereco()
                       .setCep(jsonObject.getString("cep"))
                       .setLogradouro(jsonObject.getString("logradouro"))
                       .setComplemento(jsonObject.getString("complemento"))
                       .setBairro(jsonObject.getString("bairro"))
                       .setLocalidade(jsonObject.getString("localidade"))
                       .setUf(jsonObject.getString("uf"))
                       .setUnidade(jsonObject.getString("unidade"))
                       .setIbge(jsonObject.getString("ibge"))
                       .setGia(jsonObject.getString("gia"));

        }

        return endereco;
    }

    /**
     * Recupera Map<String, String> pelo CEP
     * @param cep String no formato 00000000
     * @return instancia de Map<String, String>
     */
    public static Map<String, String> getMapPorCep(String cep) {

        JsonObject jsonObject = getCepResponse(cep);

        JsonValue erro = jsonObject.get("erro");

        Map<String, String> mapa = null;
        if (erro == null) {
            mapa = new HashMap<String, String>();

            for (Iterator<Map.Entry<String,JsonValue>> it = jsonObject.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String,JsonValue> entry = it.next();
                mapa.put(entry.getKey(), entry.getValue().toString());
            }
        }

        return mapa;
    }

    private static JsonObject getCepResponse(String cep) {

        JsonObject responseJO = null;

        try {
            if (!ValidaCepUtil.validaCep(cep)) {
                throw new RuntimeException("Formato de CEP inválido");
            }

            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet("https://viacep.com.br/ws/"+cep+"/json");
            HttpResponse response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            responseJO = Json.createReader(entity.getContent()).readObject();

            httpclient.close();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseJO;
    }
}
