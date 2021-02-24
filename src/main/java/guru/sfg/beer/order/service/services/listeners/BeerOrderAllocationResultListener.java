package guru.sfg.beer.order.service.services.listeners;

import com.kkukielka.brewery.model.events.AllocateOrderResult;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationResultListener {
    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result) {
        // successful allocation
        if (!result.getAllocationError() && !result.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPassed(result.getBeerOrderDto());
        }
        // pending inventory
        else if (!result.getAllocationError() && result.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPendingInventory(result.getBeerOrderDto());
        }
        // allocation error
        else if (result.getAllocationError()) {
            beerOrderManager.beerOrderAllocationFailed(result.getBeerOrderDto());
        }
    }
}
