package br.com.wlsc.api.domain.payment;

import br.com.wlsc.api.domain.summary.Summary;
import br.com.wlsc.api.domain.summary.SummaryDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.Objects;

@Service
@Slf4j
public class PaymentService {

    private final DataSource dataSource;

    @Autowired
    public PaymentService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void savePayment(Payment payment) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO PAYMENTS(correlation_id, amount, requested_at, processor) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, payment.getCorrelationId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setTimestamp(3, Timestamp.from(payment.getRequestedAt()));
            stmt.setString(4, payment.getProcessor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SummaryDetail getSummaryDefault(Instant from, Instant to) {
        String sql = "SELECT COUNT(*) as totalRequest, COALESCE(SUM(amount), 0) as totalAmount FROM PAYMENTS WHERE processor = 'DEFAULT' AND requested_at >= ? AND requested_at <= ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.from(Objects.isNull(from) ? Instant.now().minusSeconds(180L) : from));
            stmt.setTimestamp(2, Timestamp.from(Objects.isNull(to) ? Instant.now() : to));

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return new SummaryDetail(resultSet.getInt("totalRequest"),
                        resultSet.getBigDecimal("totalAmount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SummaryDetail(0, BigDecimal.ZERO);
    }

    public SummaryDetail getSummaryFallback(Instant from, Instant to) {
        String sql = "SELECT COUNT(*) as totalRequest, COALESCE(SUM(amount), 0) as totalAmount FROM PAYMENTS WHERE processor = 'FALLBACK' AND requested_at >= ? AND requested_at <= ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.from(Objects.isNull(from) ? Instant.now().minusSeconds(180L) : from));
            stmt.setTimestamp(2, Timestamp.from(Objects.isNull(to) ? Instant.now() : to));
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return new SummaryDetail(resultSet.getInt("totalRequest"),
                        resultSet.getBigDecimal("totalAmount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SummaryDetail(0, BigDecimal.ZERO);
    }

    public void purgePayments() {
        String sql = "DELETE FROM PAYMENTS";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            int rows = stmt.executeUpdate();
            log.info("{} payments excluded", rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Summary getSummary(Instant from, Instant to) {
        return new Summary(getSummaryDefault(from, to), getSummaryFallback(from, to));
    }
}
