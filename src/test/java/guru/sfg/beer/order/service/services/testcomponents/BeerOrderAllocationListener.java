package guru.sfg.beer.order.service.services.testcomponents;

import com.kkukielka.brewery.model.events.AllocateOrderRequest;
import com.kkukielka.brewery.model.events.AllocateOrderResult;
import guru.sfg.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message message) {
        AllocateOrderRequest request = (AllocateOrderRequest) message.getPayload();

        System.out.println("Running Test Component: BeerOrderAllocationListener");

        request.getBeerOrderDto().getBeerOrderLines().forEach(orderLine ->
            orderLine.setQuantityAllocated(orderLine.getOrderQuantity()));

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                AllocateOrderResult.builder()
                        .allocationError(false)
                        .pendingInventory(false)
                        .beerOrderDto(request.getBeerOrderDto())
                        .build());
    }
}
