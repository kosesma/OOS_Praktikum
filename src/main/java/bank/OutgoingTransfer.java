package bank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OutgoingTransfer extends Transfer{
    public OutgoingTransfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description, sender, recipient);
    }

    @Override
    public double calculate(){

        return -super.calculate();
    }
}
