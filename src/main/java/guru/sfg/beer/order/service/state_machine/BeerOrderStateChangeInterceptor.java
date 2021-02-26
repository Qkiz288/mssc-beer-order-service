package guru.sfg.beer.order.service.state_machine;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state, Message<BeerOrderEventEnum> message,
        Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition, StateMachine<BeerOrderStatusEnum,
            BeerOrderEventEnum> stateMachine) {

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) message.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    log.debug(String.format("Saving state for id: %s, status: %s", orderId, state.getId()));

//                    BeerOrder beerOrder = beerOrderRepository.getOne(UUID.fromString(orderId));
//                    beerOrder.setOrderStatus(state.getId());
//                    beerOrderRepository.saveAndFlush(beerOrder);
                    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(orderId));
                    beerOrderOptional.ifPresentOrElse(beerOrder -> {
                        beerOrder.setOrderStatus(state.getId());
                        beerOrderRepository.saveAndFlush(beerOrder);
                    }, () -> log.error(String.format("orderId = %s not found", orderId)));
                });
    }
}
