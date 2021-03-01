package guru.sfg.beer.order.service.services.testcomponents;

import com.kkukielka.brewery.model.events.ValidateOrderRequest;
import com.kkukielka.brewery.model.events.ValidateOrderResult;
import guru.sfg.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message message) {
        System.out.println("Running Test Component: BeerOrderValidationListener");
        boolean isValid = true;

        ValidateOrderRequest request = (ValidateOrderRequest) message.getPayload();
        if (request.getBeerOrderDto().getCustomerRef() != null &&
            request.getBeerOrderDto().getCustomerRef().equals("fail-validation")) {
            isValid = false;
        }

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                        .isValid(isValid)
                        .orderId(request.getBeerOrderDto().getId())
                        .build());
    }

}
