package com.healthcare.claims.api.members.seed;

import com.healthcare.claims.api.members.model.Member;
import com.healthcare.claims.api.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberSeeder implements CommandLineRunner {

    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            log.info("Members already seeded, skipping. Count: {}", memberRepository.count());
            return;
        }
        log.info("Seeding sample members...");
        seedMembers();
        log.info("Seeded {} members", memberRepository.count());
    }

    private void seedMembers() {
        // TNT-001 = "Horizon Health Partners" (matches API-Tenants TenantSeeder)
        // MBR-001 through MBR-006 are referenced by API-Claims ClaimSeeder
        String tenantId = "TNT-001";

        createMember(tenantId, "MBR-001", "John", "Smith", "1985-06-15",
                "john.smith@email.com", "555-0101", "100 Main St, Boston, MA 02101",
                "POL-2026-001", "ACTIVE", "1234");
        createMember(tenantId, "MBR-002", "Jane", "Doe", "1990-03-22",
                "jane.doe@email.com", "555-0102", "200 Oak Ave, Boston, MA 02102",
                "POL-2026-002", "ACTIVE", "5678");
        createMember(tenantId, "MBR-003", "Robert", "Johnson", "1978-11-08",
                "robert.j@email.com", "555-0103", "300 Elm St, Cambridge, MA 02139",
                "POL-2026-003", "ACTIVE", "9012");
        createMember(tenantId, "MBR-004", "Emily", "Wilson", "1995-01-30",
                "emily.w@email.com", "555-0104", "400 Pine Rd, Brookline, MA 02445",
                "POL-2026-004", "ACTIVE", "3456");
        createMember(tenantId, "MBR-005", "Michael", "Brown", "1982-07-12",
                "michael.b@email.com", "555-0105", "500 Maple Dr, Somerville, MA 02143",
                "POL-2026-005", "ACTIVE", "7890");
        createMember(tenantId, "MBR-006", "Sarah", "Davis", "1988-09-25",
                "sarah.d@email.com", "555-0106", "600 Cedar Ln, Newton, MA 02458",
                "POL-2026-006", "ACTIVE", "2345");
        createMember(tenantId, "MBR-007", "David", "Martinez", "1972-04-18",
                "david.m@email.com", "555-0107", "700 Birch Ct, Quincy, MA 02169",
                "POL-2026-007", "ACTIVE", "6789");
        createMember(tenantId, "MBR-008", "Lisa", "Anderson", "1993-12-03",
                "lisa.a@email.com", "555-0108", "800 Walnut St, Medford, MA 02155",
                "POL-2026-008", "INACTIVE", "0123");
        createMember(tenantId, "MBR-009", "James", "Taylor", "1980-08-20",
                "james.t@email.com", "555-0109", "900 Spruce Ave, Waltham, MA 02451",
                "POL-2026-009", "ACTIVE", "4567");
        createMember(tenantId, "MBR-010", "Maria", "Garcia", "1987-02-14",
                "maria.g@email.com", "555-0110", "1000 Willow Way, Arlington, MA 02474",
                "POL-2026-010", "ACTIVE", "8901");
    }

    private void createMember(String tenantId, String memberId, String firstName, String lastName,
                              String dob, String email, String phone, String address,
                              String policyNumber, String status, String ssnLast4) {
        Member member = Member.builder()
                .tenantId(tenantId)
                .memberId(memberId)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.parse(dob))
                .email(email)
                .phone(phone)
                .address(address)
                .policyNumber(policyNumber)
                .policyStatus(status)
                .ssnLast4(ssnLast4)
                .build();
        memberRepository.save(member);
    }
}
