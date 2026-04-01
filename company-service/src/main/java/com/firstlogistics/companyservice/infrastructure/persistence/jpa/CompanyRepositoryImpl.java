package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import static com.firstlogistics.companyservice.infrastructure.persistence.jpa.QCompanyJpaEntity.companyJpaEntity;

import com.firstlogistics.companyservice.application.dto.query.CompanyQueryCondition;
import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {

    private final CompanyJpaRepository companyJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Company save(Company company) {
        CompanyJpaEntity jpaEntity = CompanyMapper.toJpaEntity(company);
        CompanyJpaEntity savedEntity = companyJpaRepository.save(jpaEntity);
        return CompanyMapper.toDomain(savedEntity);
    }

    @Override
    public Page<Company> findAll(CompanyQueryCondition condition, Pageable pageable) {
        BooleanBuilder builder = buildPredicate(condition);

        List<Company> content = queryFactory
                .selectFrom(companyJpaEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(resolveOrderSpecifier(pageable.getSort()))
                .fetch()
                .stream()
                .map(CompanyMapper::toDomain)
                .toList();

        long total = queryFactory
                .select(companyJpaEntity.count())
                .from(companyJpaEntity)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder buildPredicate(CompanyQueryCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(companyJpaEntity.deletedAt.isNull());

        if (condition.keyword() != null && !condition.keyword().isBlank()) {
            builder.and(companyJpaEntity.name.containsIgnoreCase(condition.keyword()));
        }
        if (condition.type() != null) {
            builder.and(companyJpaEntity.type.equalsIgnoreCase(condition.type()));
        }
        if (condition.hubId() != null) {
            builder.and(companyJpaEntity.hubId.eq(condition.hubId()));
        }
        if (condition.status() != null) {
            builder.and(companyJpaEntity.status.eq(condition.status()));
        }

        return builder;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> {
                    boolean isAsc = order.isAscending();
                    return switch (order.getProperty()) {
                        case "updatedAt" -> isAsc ? companyJpaEntity.updatedAt.asc() : companyJpaEntity.updatedAt.desc();
                        default -> isAsc ? companyJpaEntity.createdAt.asc() : companyJpaEntity.createdAt.desc();
                    };
                })
                .findFirst()
                .orElse(companyJpaEntity.createdAt.desc());
    }
}
