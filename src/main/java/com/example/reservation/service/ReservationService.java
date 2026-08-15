package com.example.reservation.service;

import com.example.reservation.dto.ReservationForm;
import com.example.reservation.dto.ReservationResponse;
import com.example.reservation.entity.Member;
import com.example.reservation.entity.Reservation;
import com.example.reservation.entity.Room;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    public void create(Long memberId, ReservationForm form) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Room room = roomRepository.findById(form.getRoomId()).orElseThrow();

        if (!form.getStartTime().isBefore(form.getEndTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }

        List<Reservation> reservations =
                reservationRepository.findByRoomIdAndReservationDate(
                        form.getRoomId(),
                        form.getReservationDate()
                );

        for (Reservation reservation : reservations) {
            boolean overlap =
                    form.getStartTime().isBefore(reservation.getEndTime())
                            && form.getEndTime().isAfter(reservation.getStartTime());

            if (overlap) {
                throw new IllegalArgumentException("이미 예약된 시간입니다.");
            }
        }

        Reservation reservation = Reservation.builder()
                .member(member)
                .room(room)
                .reservationDate(form.getReservationDate())
                .startTime(form.getStartTime())
                .endTime(form.getEndTime())
                .build();

        reservationRepository.save(reservation);
    }

    public List<ReservationResponse> findByMemberId(Long memberId) {

        return reservationRepository.findByMemberId(memberId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoom().getId())
                .roomName(reservation.getRoom().getName())
                .reservationDate(reservation.getReservationDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .build();
    }

    public void delete(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow();

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }

        reservationRepository.delete(reservation);
    }

    public ReservationResponse findById(Long id, Long memberId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow();

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예약만 수정할 수 있습니다.");
        }

        return mapToResponse(reservation);
    }

    public void update(Long id, Long memberId, ReservationForm form) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow();

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예약만 수정할 수 있습니다.");
        }

        Room room = roomRepository.findById(form.getRoomId())
                .orElseThrow();

        if (!form.getStartTime().isBefore(form.getEndTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }

        List<Reservation> reservations =
                reservationRepository.findByRoomIdAndReservationDate(
                        form.getRoomId(),
                        form.getReservationDate()
                );

        for (Reservation other : reservations) {
            if (other.getId().equals(id)) {
                continue;
            }

            boolean overlap =
                    form.getStartTime().isBefore(other.getEndTime())
                    && form.getEndTime().isAfter(other.getStartTime());

            if (overlap) {
                throw new IllegalArgumentException("이미 예약된 시간입니다.");
            }
        }

        reservation.setRoom(room);
        reservation.setReservationDate(form.getReservationDate());
        reservation.setStartTime(form.getStartTime());
        reservation.setEndTime(form.getEndTime());

        reservationRepository.save(reservation);
    }
}
