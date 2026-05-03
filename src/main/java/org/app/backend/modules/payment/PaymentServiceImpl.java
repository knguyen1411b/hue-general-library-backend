package org.app.backend.modules.payment;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.payment.dto.PaymentCreateDTO;
import org.app.backend.modules.payment.dto.PaymentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final ModelMapper modelMapper;

  public PaymentServiceImpl(PaymentRepository paymentRepository, ModelMapper modelMapper) {
    this.paymentRepository = paymentRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Page<PaymentDTO> findAll(
      Pageable pageable, UUID userId, PaymentType paymentType, PaymentStatus paymentStatus) {
    if (userId != null) {
      return paymentRepository
          .findByUserId(userId, pageable)
          .map(p -> modelMapper.map(p, PaymentDTO.class));
    }
    if (paymentType != null) {
      return paymentRepository
          .findByPaymentType(paymentType, pageable)
          .map(p -> modelMapper.map(p, PaymentDTO.class));
    }
    if (paymentStatus != null) {
      return paymentRepository
          .findByPaymentStatus(paymentStatus, pageable)
          .map(p -> modelMapper.map(p, PaymentDTO.class));
    }
    return paymentRepository.findAll(pageable).map(p -> modelMapper.map(p, PaymentDTO.class));
  }

  @Override
  public PaymentDTO findById(UUID id) {
    Payment payment =
        paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
    return modelMapper.map(payment, PaymentDTO.class);
  }

  @Override
  public PaymentDTO create(PaymentCreateDTO dto, CustomUserDetails actor) {
    Payment payment = modelMapper.map(dto, Payment.class);
    payment.setPaymentStatus(PaymentStatus.PENDING);
    Payment saved = paymentRepository.save(payment);
    return modelMapper.map(saved, PaymentDTO.class);
  }

  @Override
  public PaymentDTO confirm(UUID id, CustomUserDetails actor) {
    Payment payment =
        paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
    payment.setPaymentStatus(PaymentStatus.SUCCESS);
    Payment saved = paymentRepository.save(payment);
    return modelMapper.map(saved, PaymentDTO.class);
  }

  @Override
  public void delete(UUID id, CustomUserDetails actor) {
    Payment payment =
        paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
    paymentRepository.delete(payment);
  }
}
