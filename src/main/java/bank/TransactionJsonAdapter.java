package bank;
import com.google.gson.*;

import java.lang.reflect.Type;

public class TransactionJsonAdapter implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

    /**
     * @param jsonElement
     * @param type
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String className = jsonObject.get("CLASSNAME").getAsString();
        JsonElement instanceElement = jsonObject.get("INSTANCE");

        try {
            // Use reflection to find the class corresponding to the className
            Class<?> clazz = Class.forName("bank." + className);  // Find the class by name

            // Deserialize the INSTANCE data into the corresponding class object
            return context.deserialize(instanceElement, clazz);
        } catch (ClassNotFoundException e) {

            throw new JsonParseException("Class not found: " + className, e);
        }
    }



    /**
     * @param transaction
     * @param type
     * @param jsonSerializationContext
     * @return
     */
    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson(); // Default Gson instance
        JsonElement transactionElement = gson.toJsonTree(transaction);

        //String className = transaction.getClass().getSimpleName(); // Get simple class name
        //String formattedClassName = className.replaceAll("([a-z])([A-Z])", "$1 $2");

        jsonObject.addProperty("CLASSNAME", transaction.getClass().getSimpleName());
        jsonObject.add("INSTANCE", transactionElement);

        return jsonObject;
    }
}
