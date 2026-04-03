import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { AuthHeader } from '../components/molecules/AuthHeader'
import { MagicLinkVerifyHandler } from '../components/organisms/MagicLinkVerifyHandler'

export const MagicLinkVerifyPage = () => {
  return (
    <AuthLayout>
      <PageMeta title="Verifying Magic Link" />
      <AuthCard>
        <AuthHeader title="Verifying your magic link" subtitle="" showLogo={false} />
        <MagicLinkVerifyHandler />
      </AuthCard>
    </AuthLayout>
  )
}
