package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import static com.firstlogistics.companyservice.infrastructure.persistence.jpa.QCompanyJpaEntity.companyJpaEntity;

import com.firstlogistics.companyservice.application.dto.query.CompanyQueryCondition;
import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

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

        List<CompanyJpaEntity> entities = queryFactory
                .selectFrom(companyJpaEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(resolveOrderSpecifiers(pageable.getSort()))
                .fetch();

        List<Company> content = new ArrayList<>(entities.size());
        for (CompanyJpaEntity entity : entities) {
            content.add(CompanyMapper.toDomain(entity));
        }

        Long totalCount = queryFactory
                .select(companyJpaEntity.count())
                .from(companyJpaEntity)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<Company> findById(UUID companyId) {
        return companyJpaRepository.findByIdAndDeletedAtIsNull(companyId)
                .map(CompanyMapper::toDomain);
    }

    @Override
    public Optional<Company> findByManagerId(UUID managerId) {
        return companyJpaRepository.findByManagerIdAndDeletedAtIsNull(managerId)
                .map(CompanyMapper::toDomain);
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

    private OrderSpecifier<?>[] resolveOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> specifiers = sort.stream()
                .map(order -> {
                    boolean isAsc = order.isAscending();
                    return switch (order.getProperty()) {
                        case "updatedAt" ->
                                isAsc ? companyJpaEntity.updatedAt.asc() : companyJpaEntity.updatedAt.desc();
                        default -> isAsc ? companyJpaEntity.createdAt.asc() : companyJpaEntity.createdAt.desc();
                    };
                })
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        if (specifiers.isEmpty()) {
            specifiers.add(companyJpaEntity.createdAt.desc());
        }
        specifiers.add(companyJpaEntity.id.asc());

        return specifiers.toArray(new OrderSpecifier[0]);
    }
}
