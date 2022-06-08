package shopmall;

import shopmall.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyHandler{
    @Autowired
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderPlaced_StartDelivery(@Payload OrderPlaced orderPlaced){

        if(orderPlaced.isMe()){
            System.out.println("##### listener StartDelivery : " + orderPlaced.toJson());
            Delivery delivery = new Delivery();
            delivery.setOrderId(orderPlaced.getId());
            delivery.setProductId(orderPlaced.getProductId());
            delivery.setProductName(orderPlaced.getProductName());
            deliveryRepository.save(delivery);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_DeleteDelivery(@Payload OrderCancelled orderCancelled){
        if(orderCancelled.isMe()){
            List<Delivery> deliveryList = deliveryRepository.findByOrderId(orderCancelled.getId());
            if ((deliveryList != null) && !deliveryList.isEmpty()){
                deliveryRepository.deleteAll(deliveryList);
            }
        }
    }

}
