package guru.sfg.beer.order.service.services;

import com.kkukielka.brewery.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerPagedList listCustomers(Pageable pageable);
}
